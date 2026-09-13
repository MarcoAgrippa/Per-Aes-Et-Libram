> Status: predlog plana, nije odobren za implementaciju. Poslednja izmena: 2026-09-13.

# Potpisivanje PDF dokumenata kvalifikovanim elektronskim potpisom

## Kontekst

Aplikacija je alat advokata za vođenje predmeta, ročišta i rokova. Za elektronsku
komunikaciju sa sudom (dostava podneska mejlom, aplikacija *eSud*) uslov je **kvalifikovani
elektronski potpis**. Danas aplikacija ne ume ništa sa PDF-om: prilozi su isključivo JPEG
slike iz ML Kit skenera, pregledač je Coil `AsyncImage`, a u projektu ne postoji ni jedna
PDF biblioteka, ni mrežni sloj, ni kripto zavisnost, ni `FileProvider`.

Cilj: korisnik izabere PDF sa telefona, dovede ga do kvalifikovanog potpisa, i dobije
potpisan fajl koji može poslati sudu — sve iz aplikacije.

### Odluke korisnika

| Pitanje | Odluka |
|---|---|
| Nivo potpisa | Kvalifikovani (QES), ključ u klaudu kod izdavaoca |
| Izdavalac | **ITE / eUprava, sertifikat u klaudu + ConsentID** |
| Izvor dokumenta | Korisnik bira PDF sa telefona (SAF) |
| Obim | **Oboje**: odmah predaja spoljašnjem potpisniku; pravi API iza interfejsa, ukopčava se kasnije |

### Ograničenje koje oblikuje ceo dizajn

ITE ima REST API za potpisivanje u klaudu, ali je javno opisan kao razvijen **za potrebe
organa i jedinica lokalne samouprave koji imaju svoja softverska rešenja**. Nema javnog
sandbox-a, nema samouslužnog ključa, nema javne dokumentacije. Privatna advokatska
aplikacija realno neće dobiti pristup bez posebnog dogovora sa Kancelarijom za IT i eUpravu.

Zato je **potpisivanje unutar aplikacije tretirano kao neizvesno**, a ne kao osnova plana.
Sve što se može isporučiti bez ugovora gradi se sada; sam poziv izdavaocu stoji iza
`RemoteSigner` interfejsa i zamenjuje se jednom klasom ako/kad pristup bude odobren.

**Bitno za tajnost spisa:** u budućem API putu aplikacija šalje izdavaocu **samo heš
dokumenta**, nikad sam dokument. To je smisao CSC `signHash` modela i jedini oblik koji je
prihvatljiv za privilegovanu prepisku sa klijentom. Rešenja tipa eID Easy, koja uploaduju ceo
PDF na tuđi server, su iz tog razloga odbačena.

---

## Šta se gradi

### Tok korisnika (v1)

1. Fioka → **Potpisivanje** → lista dokumenata sa statusom.
2. FAB → sistemski birač fajlova (samo `application/pdf`) → dokument se kopira u privatno
   skladište aplikacije, red u bazi dobija status `NEPOTPISAN`.
3. Detalj dokumenta: pregled stranica PDF-a, podaci o fajlu, dugmad za akcije.
4. **Pošalji na potpis** → izvoz kopije na mesto koje korisnik izabere (npr. *Preuzimanja*)
   + otvaranje portala za potpis u klaudu u pregledaču. Status → `POSLAT_NA_POTPIS`.
5. Korisnik potpiše na portalu (ConsentID odobrenje na telefonu), preuzme potpisan PDF.
6. **Uvezi potpisan** → birač fajlova → aplikacija pročita potpise iz PDF-a, upiše potpisnika
   i vreme potpisa, status → `POTPISAN`.
7. **Podeli** → `ACTION_SEND` preko `FileProvider`-a → mejl sudu.

Korak 4–6 je zaobilaznica, i treba da bude vidljivo označena kao takva u UI-ju. Kad
`RemoteSigner` dobije pravu implementaciju, koraci 4–6 se svode na jedno dugme
**Potpiši** i ConsentID notifikaciju.

### Namerno van obima v1

- **Vezivanje dokumenta za predmet** — korisnik je izabrao samo „bira fajl sa telefona".
  Tabela se pravi bez `caseId`; kasnija migracija dodaje kolonu i FK kad zatreba.
- **Pozicioniranje grafičkog prikaza potpisa** — u putu sa predajom to radi sam portal, pa bi
  bio bačen posao. Pripada API putu.
- **Puna kriptografska validacija** (lanac poverenja do ITE korena, CRL/OCSP, vremenski žig).
  v1 čita i prikazuje *prijavljene* podatke o potpisu i proverava da li `ByteRange` pokriva ceo
  fajl. UI to mora eksplicitno reći, da se ne stvori lažan utisak provere.
- Skeniranje kamerom u PDF (`RESULT_FORMAT_PDF` postoji u već prisutnom ML Kit skeneru —
  lak dodatak kasnije).

---

## Izvedba

Prati se postojeći obrazac iz funkcije **Notary** (sedam fajlova + dve registracije) i
obrazac apstrakcije SDK-a iz **OCR**-a (`TextRecognizer` / `MlKitTextRecognizer` / `OcrModule`).
Detalji oba obrasca su u Dodatku A.

### 1. Domen — `domain/model/Dokument.kt`

Data klasa `Dokument(id, naziv, fileName, status, potpisnik?, vremePotpisa?, napomena?, datumKreiranja, datumIzmene)`,
`id: Long = 0` sentinel. Novi enum `StatusPotpisa { NEPOTPISAN, POSLAT_NA_POTPIS, POTPISAN }`
ide u `domain/model/Enums.kt`.

### 2. Domen — `domain/signing/`

```kotlin
/** Podaci o jednom potpisu pročitani iz PDF-a. Bez provere lanca poverenja. */
data class PodaciOPotpisu(
    val potpisnik: String?, val razlog: String?, val mesto: String?,
    val vremePotpisa: Instant?, val pokrivaCeoDokument: Boolean
)

/** Čita potpise iz PDF-a. Apstrakcija nad PDF bibliotekom — odvojena radi testabilnosti. */
interface SignatureInspector {
    suspend fun inspect(file: File): List<PodaciOPotpisu>
}

/**
 * Kvalifikovano potpisivanje na daljinu kod izdavaoca (CSC-stil API).
 * Ključ nikad ne napušta HSM izdavaoca — šalje se samo heš, nikad sam dokument.
 * U v1 nema produkcijske implementacije (ITE API traži poseban pristup) — vidi [NedostupanRemoteSigner].
 */
interface RemoteSigner {
    suspend fun dostupan(): Boolean
    suspend fun sertifikati(): List<SigningCredential>
    suspend fun potpisiHes(credentialId: String, hes: ByteArray, algoritam: HashAlgorithm): ByteArray
}
```

`RemoteSigner` je jedina tačka koju budući ITE/CSC klijent treba da implementira. Ostatak
funkcije ga ne poznaje osim kroz `dostupan()`, koji u v1 vraća `false` i gasi dugme
„Potpiši u aplikaciji".

### 3. Podaci

- `data/local/entity/DokumentEntity.kt` — `@Entity(tableName = "dokumenti")` + `toDomain()` / `toEntity()`
  ekstenzije u istom fajlu, po `NotaryEntity` šablonu.
- `data/local/dao/DokumentDao.kt` — `observeAll(): Flow<List<DokumentEntity>>` sortirano po
  `datumIzmene DESC`, `getById`, `insert`, `update`, `delete`.
- `data/repository/DokumentRepositoryImpl.kt` — kanonski `save()` upsert; `delete()` **prvo briše
  fajl sa diska pa red**, tačno kao `PrilogRepositoryImpl` (Room CASCADE ne dira disk).
- `data/local/storage/DocumentFileStore.kt` — kopija obrasca `AttachmentFileStore`:
  privatno `context.filesDir/dokumenti/<UUID>.pdf`, `import(sourceUri)` kroz
  `contentResolver.openInputStream`, `export(dokument, targetUri)` kroz `openOutputStream`,
  `resolve()` ne-suspend (čista matematika nad putanjom, bezbedna iz kompozicije), `delete()`.
- `data/signing/PdfBoxSignatureInspector.kt` — jedini fajl koji importuje `com.tom_roush.pdfbox.*`;
  `@IoDispatcher` + `withContext`, greške progutane u `runCatching`, po uzoru na `MlKitTextRecognizer`.
- `data/signing/NedostupanRemoteSigner.kt` — `dostupan() = false`, ostale metode bacaju
  `UnsupportedOperationException` uz KDoc koji objašnjava zašto.

### 4. Baza

`Converters.kt`: novi par za `StatusPotpisa` (`.name` / `valueOf`).
`Migrations.kt`: `MIGRATION_4_5` sa `CREATE TABLE IF NOT EXISTS dokumenti (...)`, ručni SQL sa
backtick identifikatorima, po uzoru na `MIGRATION_3_4`.
`AppDatabase.kt`: `version = 5`, novi entitet + `abstract fun dokumentDao()`.
`DatabaseModule.kt`: `.addMigrations(..., MIGRATION_4_5)` i `@Provides fun provideDokumentDao(db)`.
Nova šema se eksportuje u `app/schemas/.../5.json` — **mora se commitovati**.
`RepositoryModule.kt`: `@Binds @Singleton` za `DokumentRepository`.

### 5. Novi Hilt modul — `di/SigningModule.kt`

`@Binds @Singleton` za `SignatureInspector` → `PdfBoxSignatureInspector` i `RemoteSigner` →
`NedostupanRemoteSigner`. Osamnaest linija, po uzoru na `OcrModule`.

### 6. UI — `ui/documents/`

Bez `UiState` klasa i bez sealed događaja — u ovoj bazi koda ih nema nigde.

- `DocumentListViewModel` — `observeAll()` kroz `.stateIn(viewModelScope, WhileSubscribed(5000), emptyList())`,
  `fun uvezi(uri: Uri)`, `fun obrisi(dokument)`.
- `DocumentListScreen(onOpenDrawer, onOpenDocument)` — `Scaffold` + `TopAppBar` sa `Menu` ikonom,
  FAB koji pokreće `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument())`
  sa `arrayOf("application/pdf")`, status čip po redu, `AlertDialog` za brisanje, `EmptyState`
  iz `ui/common`.
- `DocumentDetailViewModel` — `documentId` iz `SavedStateHandle`, akcije `posaljiNaPotpis`,
  `uveziPotpisan(uri)`, `renderPage(index)`.
- `DocumentDetailScreen(onBack, onShare)` — pregled stranica preko **`android.graphics.pdf.PdfRenderer`**
  (ugrađen u Android od API 21, minSdk je 26 — nova zavisnost nije potrebna za prikaz).
  Renderovanje ide van glavne niti, `PdfRenderer` nije thread-safe i drži jednu otvorenu stranicu;
  zumiranje/pomeranje preuzeti iz `AttachmentViewerScreen` (`rememberTransformableState`).
  Kartica sa podacima o potpisu kad je status `POTPISAN`, uz jasnu napomenu da lanac poverenja
  nije proveren.

### 7. Navigacija i fioka

`NavGraph.kt`: `const val DOCUMENTS = "documents"`, `const val DOCUMENT_DETAIL = "document_detail/{documentId}"`,
`fun documentDetail(documentId: Long)`, argument `NavType.LongType`.
`AppDrawer.kt`: nov unos u `DrawerDestination` — labela **„Potpisivanje"**, ikona
`Icons.Default.Draw` (ili `EditNote`), plus ruta u `TOP_LEVEL_ROUTES`.

### 8. Manifest i deljenje

`AndroidManifest.xml` dobija `FileProvider` (prvi u projektu):

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
               android:resource="@xml/file_paths" />
</provider>
```

+ `res/xml/file_paths.xml` sa `<files-path name="dokumenti" path="dokumenti/" />`.
Deljenje: `ACTION_SEND`, `type="application/pdf"`, `FLAG_GRANT_READ_URI_PERMISSION`.
Izvoz na korisnikovo mesto: `ActivityResultContracts.CreateDocument("application/pdf")`.
Otvaranje portala: `ACTION_VIEW` — URL portala držati kao **jednu konstantu** u
`ui/documents/`, jer se adresa servisa menja i treba je potvrditi pre isporuke.

### 9. Build

`gradle/libs.versions.toml`: `pdfboxAndroid = "2.0.27.0"` + `pdfbox-android = { module = "com.tom-roush:pdfbox-android", version.ref = "pdfboxAndroid" }`.
`PerAesEtLibramApplication.onCreate()`: `PDFBoxResourceLoader.init(applicationContext)`.

> **Trošak koji treba svesno prihvatiti:** PdfBox-Android nosi resurse za fontove i osetno
> uvećava APK (red veličine 10 MB). U v1 ga koristi samo čitanje potpisa. Alternativa je
> odložiti biblioteku i v1 ostaviti bez prikaza podataka o potpisu. Preporuka je ipak uzeti je
> odmah, jer je to ista biblioteka koju API put traži za PAdES pripremu
> (`saveIncrementalForExternalSigning`, potvrđeno da postoji u `PDDocument` na master grani).

### 10. Testovi

Prati se zatečeni stil: JUnit 4 + Truth, čista logika, imena metoda kao srpske rečenice u
backtick-ovima. `androidTest` ne postoji i ne uvodi se.

- `DocumentNamingTest` — čišćenje imena iz SAF-a, generisanje `<naziv>-potpisan.pdf`,
  ponašanje kad ime nema ekstenziju ili sadrži nedozvoljene znakove.
- `StatusPotpisaTest` — dozvoljeni prelazi statusa i odbijanje nedozvoljenih.

`PdfBoxSignatureInspector` traži pravi PDF fajl, pa se pokriva ručnom proverom (dole).

---

## Provera

1. `./gradlew assembleDebug` i `./gradlew testDebugUnitTest` prolaze.
2. **Migracija**: instalirati prethodni build, uneti nekoliko predmeta, pa preko njega
   instalirati novi — aplikacija se otvara, podaci ostaju (nema `fallbackToDestructiveMigration`,
   pogrešna migracija ruši aplikaciju pri startu). Potvrditi da je `app/schemas/.../5.json` nastao.
3. **Uvoz**: Potpisivanje → FAB → izabrati PDF → pojavljuje se u listi sa statusom `NEPOTPISAN`;
   otvoriti detalj i proveriti da se stranice iscrtavaju i da se lista skroluje bez trzanja.
4. **Predaja**: „Pošalji na potpis" → izvoz u *Preuzimanja* → portal se otvara u pregledaču →
   potpisati stvarnim sertifikatom u klaudu uz ConsentID odobrenje → preuzeti rezultat.
5. **Povratak**: „Uvezi potpisan" → izabrati preuzeti fajl → status `POTPISAN`, prikazani
   potpisnik i vreme, `pokrivaCeoDokument = true`.
6. **Negativan slučaj**: uvesti nepotpisan PDF kao „potpisan" — mora ostati bez podataka o
   potpisu i jasno to reći, a ne tiho prijaviti uspeh.
7. **Deljenje**: „Podeli" → mejl klijent → potpisan PDF stiže kao prilog koji se otvara.
8. **Brisanje**: obrisati dokument pa proveriti da fajl više ne postoji u
   `filesDir/dokumenti/` (Room CASCADE ne briše disk).

---

## Rizici

| Rizik | Posledica / postupanje |
|---|---|
| ITE REST API je za državne organe | API put možda nikad ne bude moguć. Zato je v1 samostalno upotrebljiv, a `RemoteSigner` mala i zamenljiva tačka. Vredi poslati upit Kancelariji za IT i eUpravu pre nego što se uloži posao u API put. |
| PdfBox-Android uvećava APK | Svesna razmena, opisana gore. Meriti veličinu pre/posle. |
| `saveIncrementalForExternalSigning` u objavljenom artefaktu | Potvrđeno na master grani; pre oslanjanja u API putu proveriti da postoji u 2.0.27.0. Ne utiče na v1. |
| Adresa portala za potpis se menja | Držati kao jednu konstantu; potvrditi pre isporuke. |
| Prikaz podataka o potpisu deluje kao validacija | UI mora eksplicitno reći da lanac poverenja nije proveren. |
| `PdfRenderer` nije thread-safe | Jedna otvorena stranica, renderovanje serijalizovano van glavne niti. |

---
---

# Dodatak A — Zatečena arhitektura (izviđanje)

Jedan Gradle modul `:app`, namespace/applicationId `com.peraeslibram`.
Nema CLAUDE.md ni README — konvencije se čitaju iz koda i vrlo su dosledne.

## A.1 Raspored paketa

`app/src/main/java/com/peraeslibram/`

| Paket | Sadržaj |
|---|---|
| `app/` | `MainActivity.kt` (`@AndroidEntryPoint`), `PerAesEtLibramApplication.kt` (`@HiltAndroidApp`, `Configuration.Provider` za WorkManager, notifikacioni kanali, 30-dnevni `HolidayAutoSeedWorker`), `NavGraph.kt`, `AppDrawer.kt` |
| `domain/model/` | Čiste Kotlin data klase: `Case, Hearing, Deadline, DeadlineRule, DeadlineWithCase, AgendaItem, Court, Notary, NonWorkingDay, Prilog, Reminder, Enums.kt` |
| `domain/repository/` | 8 repository **interfejsa** |
| `domain/ocr/` | `TextRecognizer` (apstrakcija nad SDK), `SummonsParser`, `ParsedSummons` |
| `domain/scheduler/` | `AlarmScheduler` |
| `domain/calculator/` | `DeadlineCalculator` |
| `domain/usecase/` | 3 use case-a (izuzetak — većina VM-ova zove repozitorijum direktno) |
| `data/local/` | `AppDatabase.kt`, `converters/Converters.kt`, `dao/` (9), `entity/` (9 + mapper ekstenzije), `migration/Migrations.kt`, `seed/`, `storage/AttachmentFileStore.kt` |
| `data/repository/` | 8 `*RepositoryImpl` + `ReminderCoordinator.kt` |
| `data/ocr/` | `MlKitTextRecognizer.kt` |
| `data/notification/` | `AndroidAlarmScheduler`, `ReminderReceiver`, `BootReceiver`, `BootRescheduleWorker`, `HolidayAutoSeedWorker`, `NotificationChannels` |
| `di/` | `CoroutinesModule` (kvalifikatori `@IoDispatcher`, `@ApplicationScope`), `DatabaseModule`, `RepositoryModule`, `NotificationModule`, `OcrModule` |
| `ui/` | Paket po funkciji: `cases, hearings, deadlines, courts, notaries, dashboard, settings, attachments` + `common/` (`CommonComponents.kt`: `EmptyState`, `IconBadge`, `SectionHeader`, `InfoRow`; `DeadlineRow`, `TimeGrouping`) i `theme/` |

Pravilo slojevitosti: `ui → domain` (samo interfejsi i modeli); `data` implementira `domain`,
veže se u `di`. Nijedan `ui` fajl ne importuje `*RepositoryImpl` ni DAO.
Jedini namerni izuzetak je `AttachmentFileStore` (`data/local/storage/`), koji
`CaseDetailViewModel` i `AttachmentViewerViewModel` injektuju direktno — putanje fajlova
nisu modelovane u domenu.

## A.2 Obrazac funkcije s kraja na kraj — Notary (referentni primer)

**a. Domain model** `domain/model/Notary.kt` — data klasa, `id: Long = 0` kao sentinel za „novo",
`Instant` polja `datumKreiranja` / `datumIzmene`, srpski nazivi polja.

**b. Domain interfejs** `domain/repository/NotaryRepository.kt`:
```kotlin
interface NotaryRepository {
    fun observeAll(): Flow<List<Notary>>
    suspend fun getById(id: Long): Notary?
    suspend fun save(notary: Notary): Long
    suspend fun delete(notary: Notary)
    /** Ako u bazi još nema nijednog beležnika, upisuje ugrađeni spisak (...). */
    suspend fun ensureSeeded()
}
```
Konvencija: `observe*` vraća `Flow`, sve ostalo `suspend`; KDoc na srpskom objašnjava *zašto*.

**c. Entity + maperi u istom fajlu** `data/local/entity/NotaryEntity.kt`:
```kotlin
@Entity(tableName = "notaries")
data class NotaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val naziv: String, val adresa: String?, /* ... */ val datumIzmene: Instant
)
fun NotaryEntity.toDomain() = Notary(/* ... */)
fun Notary.toEntity() = NotaryEntity(/* ... */)
```

**d. DAO** `data/local/dao/NotaryDao.kt` — `@Query` vraća `Flow<List<Entity>>` za posmatranje,
`suspend` za ostalo, `ORDER BY naziv COLLATE NOCASE ASC`, `count()` za seeding,
`@Insert` (jedan vraća `Long`, plus `insertAll(List)`), `@Update`, `@Delete`.

**e. Repository impl** `data/repository/NotaryRepositoryImpl.kt`:
```kotlin
override suspend fun save(notary: Notary): Long {
    val entity = notary.toEntity()
    return if (entity.id == 0L) notaryDao.insert(entity) else { notaryDao.update(entity); entity.id }
}
```

**f. Hilt** — `@Binds @Singleton` u `di/RepositoryModule.kt` + `@Provides fun provideNotaryDao(db: AppDatabase) = db.notaryDao()` u `di/DatabaseModule.kt`.

**g. List ViewModel** — **u ovoj bazi koda ne postoji nijedna `UiState` data klasa ni sealed
klasa događaja.** List VM-ovi izlažu `StateFlow`-ove preko
`combine(...).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())`
plus obične funkcije za akcije:
```kotlin
@HiltViewModel
class NotaryListViewModel @Inject constructor(private val notaryRepository: NotaryRepository) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    val query: StateFlow<String> = searchQuery.asStateFlow()
    val notaries: StateFlow<List<Notary>> = combine(notaryRepository.observeAll(), searchQuery) { n, q -> /* ... */ }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    init { viewModelScope.launch { notaryRepository.ensureSeeded() } }
    fun onQueryChange(value: String) { searchQuery.value = value }
    fun delete(notary: Notary) { viewModelScope.launch { notaryRepository.delete(notary) } }
}
```

**h. Form ViewModel** — stanje forme su **javni `by mutableStateOf("")` varovi koje composable
piše direktno** (`onValueChange = { viewModel.naziv = it }`), id iz
`SavedStateHandle.get<Long>("notaryId") ?: NEW_ID`, `fun canSave(): Boolean`,
`fun save(onSaved: () -> Unit)`.

**i. Ekrani** — `NotaryListScreen(onOpenDrawer, onAddNotary, onEditNotary, viewModel = hiltViewModel())`.
Navigacija se nikad ne radi unutar composable-a, samo kroz lambde iz `NavGraph`-a.
`Scaffold` + `TopAppBar` (`Menu` ikona na top-level, `ArrowBack` na detalju), `FloatingActionButton`
za dodavanje, `AlertDialog` za potvrdu brisanja, `EmptyState` iz `ui/common`.

## A.3 Apstrakcija nad eksternim SDK-om — OCR (presedan za provajdera potpisa)

`domain/ocr/TextRecognizer.kt` — interfejs **bez ijednog SDK tipa**, samo `android.net.Uri`:
```kotlin
/** Apstrakcija nad OCR mehanizmom (u produkciji: ML Kit) — odvojena radi testabilnosti. */
interface TextRecognizer {
    /** Prepoznaje tekst na slici [imageUri]. Vraća prazan string ako ništa nije prepoznato. */
    suspend fun recognize(imageUri: Uri): String
}
```

`data/ocr/MlKitTextRecognizer.kt` — jedini fajl koji importuje `com.google.mlkit.*`. Obrasci
vredni kopiranja: injektovanje `@ApplicationContext Context` i `@IoDispatcher CoroutineDispatcher`,
`withContext(ioDispatcher)` oko blokirajućeg posla, **lazy** kreiranje klijenta uz komentar
o ceni, privatni `Task<T>.await()` most preko `suspendCancellableCoroutine`, greške progutane
kroz `runCatching { … }.getOrDefault("")`.

`di/OcrModule.kt` — ceo modul je 18 linija:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class OcrModule {
    @Binds @Singleton
    abstract fun bindTextRecognizer(impl: MlKitTextRecognizer): TextRecognizer
}
```
Potrošači injektuju `dagger.Lazy<TextRecognizer>` kad SDK ne treba konstruisati pri otvaranju
ekrana (vidi `CaseDetailViewModel`). Isti obrazac se ponavlja za `AlarmScheduler` ←
`AndroidAlarmScheduler` preko `di/NotificationModule.kt`.

## A.4 Room

- `AppDatabase.kt`: **`version = 4`**, `exportSchema = true`, `@TypeConverters(Converters::class)`,
  9 entiteta, `DATABASE_NAME = "per_aes_et_libram.db"`.
- Migracije su **ručno pisan SQL**, jedan `val MIGRATION_X_Y = object : Migration(X, Y)` po
  property-ju u `data/local/migration/Migrations.kt`; `CREATE TABLE IF NOT EXISTS` sa
  backtick identifikatorima i `CREATE INDEX IF NOT EXISTS` gde postoji FK (vidi `MIGRATION_1_2`
  za `prilozi`). `MIGRATION_3_4` je dodala `notaries`. **Nema `fallbackToDestructiveMigration`.**
- `di/DatabaseModule.kt` gradi bazu sa `.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)`
  i `RoomDatabase.Callback().onCreate` koji zove `seederProvider.get().seedOnCreate()` —
  `Provider<DatabaseSeeder>` indirekcija razbija ciklus DB↔seeder.
- Seeding: `data/local/seed/DatabaseSeeder.kt` (sveže instalacije, `@ApplicationScope` + `Dispatchers.IO`),
  plus `CourtSeedData`, `NotarySeedData` (trenutno `emptyList()`), `HolidaySeedData`,
  `DeadlineRuleSeeder`, `OrthodoxEasterCalculator`. Postojeće instalacije dobijaju podatke kroz
  `repository.ensureSeeded()` iz `init`-a list VM-a.
- `Converters.kt`: `LocalDate ↔ epochDay`, `Instant ↔ epochMilli`, `LocalDateTime ↔ epochMilli`,
  i par po enumu (`.name` / `valueOf`). **Svaki novi enum traži novi par ovde.**
- Šeme se eksportuju u `app/schemas/com.peraeslibram.data.local.AppDatabase/{1,2,3,4}.json`.

## A.5 Navigacija

`app/NavGraph.kt`: `const val NEW_ID = -1L`, pa jedan `object Routes` sa konstantama rute **i**
builder funkcijama:
```kotlin
const val NOTARIES = "notaries"
const val NOTARY_FORM = "notary_form/{notaryId}"
fun notaryForm(notaryId: Long = NEW_ID) = "notary_form/$notaryId"
```
Argumenti su uvek `NavType.LongType` kroz `navArgument`, čitaju se u VM-u iz `SavedStateHandle`.
`AppNavGraph` drži `ModalNavigationDrawer` (sa eksplicitnim `BackHandler` zbog M3 1.2.1),
`navigateTopLevel` lambdu (`popUpTo(startDestination){saveState=true}; launchSingleTop; restoreState`),
start destinacija `Routes.DASHBOARD`.

`app/AppDrawer.kt`: `val TOP_LEVEL_ROUTES: Set<String>` gejtuje geste fioke, a
`private enum class DrawerDestination(route, label, icon)` puni listu:
`"Početna"`, `"Predmeti"`, `"Rokovi"`, `"Sudovi"`, `"Javni beležnici"`, divider, `"Podešavanja"`.

## A.6 Build

`app/build.gradle.kts`: `compileSdk = 34`, `minSdk = 26`, `targetSdk = 34`, `versionCode 1 / versionName "1.0"`,
Java/Kotlin **17**, Compose compiler ext `1.5.14`,
`packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }`,
`testOptions.unitTests { isIncludeAndroidResources = true; isReturnDefaultValues = true }`.
Release: `isMinifyEnabled = false`. **Nema `signingConfigs` bloka, nema `buildTypes.debug`,
nema flavora, nema `buildConfigField`.**

`gradle/libs.versions.toml` — AGP 8.5.2, Kotlin 1.9.24, KSP 1.9.24-1.0.20, Compose BOM 2024.06.00,
navigation-compose 2.7.7, Room 2.6.1, Hilt 2.51.1 (+ hilt-navigation-compose 1.2.0, hilt-work 1.2.0),
WorkManager 2.9.1, coroutines 1.8.1, coil 2.7.0,
`play-services-mlkit-document-scanner 16.0.0`, `com.google.mlkit:text-recognition 16.0.1`;
test: junit 4.13.2, truth 1.4.2, arch-core-testing 2.2.0, coroutines-test, room-testing, espresso, compose-ui-test.

**Upadljivo nedostaje:** nema Retrofit / Ktor / OkHttp, nema kotlinx-serialization ni Moshi/Gson,
nema DataStore ni SharedPreferences, nema CameraX, **nema PDF biblioteke**, nema security-crypto /
Tink / BouncyCastle, nema biometrics biblioteke, nema Robolectric/MockK/Mockito.

## A.7 Rad sa dokumentima danas

- **Snimanje**: `ui/cases/CaseDetailScreen.kt` (~105–181). Dva
  `rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult())`
  nad `GmsDocumentScanning.getClient(options).getStartScanIntent(activity)`. Opcije:
  `setGalleryImportAllowed(true)`, `RESULT_FORMAT_JPEG`, `SCANNER_MODE_FULL`, `setPageLimit(1)`.
  **`RESULT_FORMAT_PDF` postoji u istom SDK-u i trenutno se ne koristi.**
- **Dozvole**: `requestScanOrLaunch` proverava `Manifest.permission.CAMERA` preko
  `ContextCompat.checkSelfPermission`, inače `ActivityResultContracts.RequestPermission()`.
- **Skladište**: `data/local/storage/AttachmentFileStore.kt` — privatno
  `context.filesDir/prilozi/<caseId>/<UUID>.jpg`; `importPage(caseId, sourceUri)` kopira kroz
  `contentResolver.openInputStream`; `resolve()` je jedina ne-suspend metoda (čista matematika nad
  putanjom, bezbedna iz kompozicije); `delete`/`deleteCaseDirectory` se moraju zvati eksplicitno jer
  Room CASCADE ne dira disk (poštovano u `PrilogRepositoryImpl.delete` — prvo fajl, pa red).
- **Model priloga**: `Prilog(id, caseId, naziv, fileName, datumKreiranja)` → tabela `prilozi`,
  FK na `cases` sa `onDelete = CASCADE` i `@Index("caseId")`. Prilozi vise **samo o predmetima**.
- **Pregled**: `ui/attachments/AttachmentViewerScreen.kt` — Coil `AsyncImage` nad `File`,
  pinch-zoom/pan kroz `rememberTransformableState`. **Samo slike — PDF pregledač ne postoji.**
- **AndroidManifest.xml**: **nema `FileProvider`**. Nema nijedne storage dozvole.
  Postoje: `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`,
  `VIBRATE`, `CAMERA`.
- **Nema SAF-a** (`OpenDocument`/`CreateDocument`) nigde; jedini drugi `Intent` je `ACTION_VIEW`
  ka Google Maps sa ekrana sudova/beležnika.

## A.8 Domenski modeli

- **`Case`** (`cases`): `id, naziv, brojPredmeta?, klijentIme, klijentKontakt?, sud?` (slobodan tekst,
  *nije* FK na `courts`), `tipPostupka, napomena?, status = AKTIVAN, datumKreiranja, datumIzmene`.
- **`Hearing`** (`hearings`): `id, caseId, datumVreme: LocalDateTime, sud?, sudnica?, tipRocista?,
  napomena?, status = ZAKAZANO, datumKreiranja, datumIzmene`.
- **`Deadline`** (`deadlines`): `id, caseId, tipPostupka, tipRadnje, nazivRadnjePrikaz,
  opisCustomRadnje?, datumOkidaca, brojDana, izracunatiKrajnjiDatum, originalniKrajnjiDatum?,
  krajnjiDatumPomeren, status, izvor, napomena?, datumKreiranja, datumIzmene` + `isIstekao(danas)`.
- **`DeadlineRule`**: `tipPostupka?, tipRadnje, brojDana?, nazivPrikaz, napomenaPravno?, aktivno`.
- **`Court`** / **`Notary`**: identičan oblik — `id, naziv, adresa?, telefon?, email?, napomena?,
  datumKreiranja, datumIzmene`.
- **`NonWorkingDay`**: `id, datum, naziv, tip, izvor`.
- **`Reminder`**: `id, hearingId?, deadlineId?, minutesBefore, vremeOkidanja, notifikacijaId,
  aktivan, poslat` + `init { require(...) }` XOR invarijanta.
- Enumi u `domain/model/Enums.kt`: `TipPostupka, TipRadnje, CaseStatus, HearingStatus,
  DeadlineStatus, DataSource(MANUELNO, SKENIRANJE, OCR), NonWorkingDayType, NonWorkingDaySource`.

## A.9 Testovi

`app/src/test/java/com/peraeslibram/` — tačno 4 JVM testa, sve čista logika; **nema Robolectric-a,
nema mock biblioteke, nema testova VM-ova ni repozitorijuma**:
`DeadlineCalculatorTest`, `SummonsParserTest`, `OrthodoxEasterCalculatorTest`, `TimeGroupingTest`.
JUnit 4 + Google Truth. **Imena test metoda su srpske rečenice u backtick-ovima**, npr.
``fun `latinica - numericki datum i vreme uz kljucnu rec rocista`()``.
`app/src/androidTest/` **ne postoji** iako su zavisnosti deklarisane.

## A.10 Konvencije i jezik

- `res/values/strings.xml` ima **tačno jedan string** (`app_name`). Svaki UI tekst,
  `contentDescription`, dijalog i snackbar je **hardkodovan srpski latinični literal inline u
  composable-u**. Pratiti to; ne uvoditi strings.xml za novu funkciju.
- Komentari i KDoc su na srpskom i dokumentuju *zašto*, ne *šta*.
- Domenski identifikatori srpski (`naziv`, `adresa`, `napomena`, `datumKreiranja`),
  strukturni/Android engleski (`observeAll`, `caseId`, `fileName`).
- 4 razmaka, ~110 kolona, eksplicitni pojedinačni importi po abecedi (bez wildcard-a),
  trailing-lambda stil.
- Prenos složenih podataka između ekrana ide kroz injektovani `@Singleton` držač sa jednokratnim
  `take()` (`ui/hearings/PendingSummonsPrefill.kt`), ne kroz nav argumente.
