package com.peraeslibram.data.local.seed

import com.peraeslibram.domain.model.Court
import java.time.Instant

/**
 * Ugrađeni spisak sudova opšte i posebne nadležnosti u Republici Srbiji (Vrhovni,
 * Ustavni, Upravni, apelacioni, viši, osnovni, privredni i prekršajni sudovi, sa
 * sedištima) sa zvaničnim adresama i telefonima. Koristi se za jednokratno
 * popunjavanje baze — korisnik kasnije može da doda, izmeni ili obriše sudove ručno.
 */
object CourtSeedData {

    private data class Entry(
        val naziv: String,
        val adresa: String,
        val telefon: String? = null,
        val napomena: String? = null
    )

    private val entries = listOf(
        // Vrhovni, Ustavni, Upravni i Privredni apelacioni sud
        Entry("Vrhovni sud", "Nemanjina 9, 11000 Beograd", "011/360-4606"),
        Entry("Ustavni sud", "Bulevar kralja Aleksandra 15, 11120 Beograd", "011/285-5000"),
        Entry("Upravni sud", "Nemanjina 9, 11000 Beograd", "011/360-4606"),
        Entry("Privredni apelacioni sud", "Nemanjina 9, 11000 Beograd", "011/360-4606"),

        // Apelacioni sudovi
        Entry("Apelacioni sud u Beogradu", "Nemanjina 9, 11000 Beograd", "011/363-5041"),
        Entry("Apelacioni sud u Kragujevcu", "Ulica Slobode 4, 34000 Kragujevac", "034/619-6000"),
        Entry("Apelacioni sud u Nišu", "Vojvode Radomira Putnika bb, 18101 Niš", "018/506-801"),
        Entry("Apelacioni sud u Novom Sadu", "Sutjeska 3, 21101 Novi Sad", "021/4876-100"),

        // Viši sudovi
        Entry("Viši sud u Beogradu", "Savska 17a, 11000 Beograd", "011/360-1400"),
        Entry("Viši sud u Valjevu", "Karađorđeva 48, 14000 Valjevo", "014/294-294"),
        Entry("Viši sud u Vranju", "Kralja Stefana Prvovenčanog 1, 17501 Vranje", "017/422-710"),
        Entry("Viši sud u Zaječaru", "Generala Gambete bb, 19000 Zaječar", "019/421-111"),
        Entry("Viši sud u Zrenjaninu", "Kej 2. oktobra 1, 23000 Zrenjanin", "023/564-712"),
        Entry("Viši sud u Jagodini", "Kneginje Milice 86, 35000 Jagodina", "035/8224-636"),
        Entry("Viši sud u Kragujevcu", "Ulica Slobode 4, 34000 Kragujevac", "034/353-593"),
        Entry("Viši sud u Kraljevu", "Karađorđeva 5, 36000 Kraljevo", "036/313-353"),
        Entry("Viši sud u Kruševcu", "Trg kosovskih junaka 3, 37000 Kruševac", "037/413-668"),
        Entry("Viši sud u Leskovcu", "Pana Đukića 13, 16000 Leskovac", "016/542-366"),
        Entry("Viši sud u Negotinu", "Trg Stevana Mokranjca 1, 19300 Negotin", "019/542-366"),
        Entry("Viši sud u Nišu", "Vožda Karađorđa 23, 18101 Niš", "018/504-207"),
        Entry("Viši sud u Novom Pazaru", "Žitni trg 16, 36300 Novi Pazar", "020/313-519"),
        Entry("Viši sud u Novom Sadu", "Sutjeska 3, 21101 Novi Sad", "021/4876-455"),
        Entry("Viši sud u Pančevu", "Vojvode Radomira Putnika 13-15, 26101 Pančevo", "013/344-366"),
        Entry("Viši sud u Pirotu", "Srpskih vladara 126, 18300 Pirot", "010/312-447"),
        Entry("Viši sud u Požarevcu", "Jovana Šerbanovića 4, 12000 Požarevac", "012/523-886"),
        Entry("Viši sud u Prokuplju", "21. srpske divizije 2, 18400 Prokuplje", "027/321-667"),
        Entry("Viši sud u Smederevu", "Trg Republike 2, 11300 Smederevo", "026/462-6666"),
        Entry("Viši sud u Somboru", "Venac vojvode Stepe Stepanovića 13, 25000 Sombor", "025/224-446"),
        Entry("Viši sud u Sremskoj Mitrovici", "Svetog Dimitrija 39, 22000 Sremska Mitrovica", "022/600-100"),
        Entry("Viši sud u Subotici", "Senćanski put 1, 24000 Subotica", "024/554-111"),
        Entry("Viši sud u Užicu", "Nade Matić 6, 31000 Užice", "031/513-105"),
        Entry("Viši sud u Čačku", "Cara Dušana 6, 32000 Čačak", "032/227-258"),
        Entry("Viši sud u Šapcu", "Gospodar Jevremova 8, 15000 Šabac", "015/346-948"),

        // Osnovni sudovi
        Entry(
            "Prvi osnovni sud u Beogradu", "Katanićeva 15, 11000 Beograd", "011/655-3700",
            "Građansko odeljenje: Bulevar Nikole Tesle 42a, Beograd"
        ),
        Entry(
            "Drugi osnovni sud u Beogradu", "Katanićeva 15, 11000 Beograd", "011/635-4553",
            "Krivično odeljenje: Savska 17a, Beograd"
        ),
        Entry(
            "Treći osnovni sud u Beogradu", "Bulevar Mihajla Pupina 16, 11000 Beograd", "011/201-8200",
            "Odeljenje: Tošin bunar 274g, Beograd (011/635-4090)"
        ),
        Entry("Osnovni sud u Aleksincu", "Ace Milojevića 2, 18220 Aleksinac", "018/804-821"),
        Entry("Osnovni sud u Aranđelovcu", "Knjaza Miloša 102, 34300 Aranđelovac", "034/6170-642"),
        Entry("Osnovni sud u Bačkoj Palanci", "Kralja Petra I 18, 21400 Bačka Palanka", "021/754-299"),
        Entry("Osnovni sud u Bečeju", "Glavna 6, 21220 Bečej", "021/6911-751"),
        Entry("Osnovni sud u Boru", "Moše Pijade 5, 19210 Bor", "030/458-490"),
        Entry("Osnovni sud u Brusu", "Mike Đorđevića bb, 37220 Brus", "037/413-600"),
        Entry("Osnovni sud u Bujanovcu", "Karađorđa Petrovića bb, 17520 Bujanovac", "017/651-021"),
        Entry("Osnovni sud u Despotovcu", "Saveza Boraca 71, 35213 Despotovac", "035/611-145"),
        Entry("Osnovni sud u Dimitrovgradu", "Trg dr Zorana Đinđića 2, 18320 Dimitrovgrad", "010/363-171"),
        Entry("Osnovni sud u Gornjem Milanovcu", "Kneza Aleksandra Karađorđevića 29, 32300 Gornji Milanovac", "032/710-217"),
        Entry("Osnovni sud u Ivanjici", "Boška Petrovića 9, 32250 Ivanjica", "032/662-390"),
        Entry("Osnovni sud u Jagodini", "Kneginje Milice 84, 35000 Jagodina", "035/221-409"),
        Entry("Osnovni sud u Kikindi", "Svetozara Miletića 1, 23300 Kikinda", "0230/423-746"),
        Entry("Osnovni sud u Knjaževcu", "Kej Dimitrija Tucovića 5, 19350 Knjaževac", "019/731-402"),
        Entry("Osnovni sud u Kragujevcu", "Ulica Slobode 4, 34000 Kragujevac", "034/335-688"),
        Entry("Osnovni sud u Kraljevu", "Pljakina 4, 36000 Kraljevo", "036/334-278"),
        Entry("Osnovni sud u Kruševcu", "Trg kosovskih junaka 3, 37000 Kruševac", "037/413-610"),
        Entry("Osnovni sud u Kuršumliji", "Palih Boraca 37, 18430 Kuršumlija", "027/381-702"),
        Entry("Osnovni sud u Lazarevcu", "Karađorđeva 19, 11550 Lazarevac", "011/8123-167"),
        Entry("Osnovni sud u Lebanu", "Cara Dušana 118, 16230 Lebane", "016/843-531"),
        Entry("Osnovni sud u Leskovcu", "Koste Stamenkovića 16, 16000 Leskovac", "016/242-812"),
        Entry("Osnovni sud u Loznici", "Jovana Cvijića 40, 15300 Loznica", "015/878-135"),
        Entry("Osnovni sud u Majdanpeku", "Trg oslobođenja bb, 19250 Majdanpek", "030/581-218"),
        Entry("Osnovni sud u Mionici", "Vojvode Mišića 28, 14242 Mionica", "014/342-1225"),
        Entry("Osnovni sud u Mladenovcu", "Kralja Aleksandra Obrenovića 76, 11400 Mladenovac", "011/8231-144"),
        Entry("Osnovni sud u Negotinu", "Trg Stevana Mokranjca 1, 19300 Negotin", "019/542-158"),
        Entry("Osnovni sud u Nišu", "Vožda Karađorđa 23, 18101 Niš", "018/504-100"),
        Entry("Osnovni sud u Novom Pazaru", "Žitni trg 16, 36300 Novi Pazar", "020/312-511"),
        Entry("Osnovni sud u Novom Sadu", "Sutjeska 3, 21101 Novi Sad", "021/4876-136"),
        Entry("Osnovni sud u Obrenovcu", "Aleksandra Ace Simovića 9a, 11500 Obrenovac", "011/8727-059"),
        Entry("Osnovni sud u Pančevu", "Vojvode Radomira Putnika 13-15, 26101 Pančevo", "013/345-481"),
        Entry("Osnovni sud u Paraćinu", "Majora Marka 1, 35250 Paraćin", "035/563-401"),
        Entry("Osnovni sud u Petrovcu na Mlavi", "Srpskih Vladara 159, 12300 Petrovac na Mlavi", "012/331-260"),
        Entry("Osnovni sud u Pirotu", "Srpskih vladara 124, 18300 Pirot", "010/321-585"),
        Entry("Osnovni sud u Požarevcu", "Trg Stevana Maksimovića 1, 12000 Požarevac", "012/544-090"),
        Entry("Osnovni sud u Požegi", "Uče Dimitrijevića 6, 31210 Požega", "031/811-355"),
        Entry("Osnovni sud u Priboju", "Vuka Karadžića 28, 31330 Priboj", "033/2445-171"),
        Entry("Osnovni sud u Prijepolju", "Valterova 171, 31300 Prijepolje", "033/712-116"),
        Entry("Osnovni sud u Prokuplju", "21. Srpske divizije 1, 18400 Prokuplje", "027/321-784"),
        Entry("Osnovni sud u Raški", "Ratka Lukovića 23, 36350 Raška", "036/736-109"),
        Entry("Osnovni sud u Rumi", "Železnička 10, 22400 Ruma", "022/2151-640"),
        Entry("Osnovni sud u Senti", "Glavni Trg 2, 24400 Senta", "024/811-094"),
        Entry("Osnovni sud u Sjenici", "Ahmeta Abdagića bb, 36310 Sjenica", "020/5741-223"),
        Entry("Osnovni sud u Smederevu", "Trg Republike 2, 11300 Smederevo", "026/613-205"),
        Entry("Osnovni sud u Somboru", "Venac vojvode Stepe Stepanovića 13, 25101 Sombor", "025/412-221"),
        Entry("Osnovni sud u Sremskoj Mitrovici", "Trg Svetog Dimitrija 39, 22000 Sremska Mitrovica", "022/600-100"),
        Entry("Osnovni sud u Staroj Pazovi", "Karađorđeva 3, 22300 Stara Pazova", "022/310-571"),
        Entry("Osnovni sud u Subotici", "Senćanski put 1, 24000 Subotica", "024/554-111"),
        Entry("Osnovni sud u Surdulici", "Srpskih Vladara bb, 17530 Surdulica", "017/825-020"),
        Entry("Osnovni sud u Trsteniku", "Doktora Milunovića bb, 37240 Trstenik", "037/713-057"),
        Entry("Osnovni sud u Ubu", "3. oktobra 4, 14210 Ub", "014/411-315"),
        Entry("Osnovni sud u Užicu", "Nade Matić 4, 31000 Užice", "031/513-105"),
        Entry("Osnovni sud u Valjevu", "Karađorđeva 50, 14000 Valjevo", "014/294-294"),
        Entry("Osnovni sud u Velikoj Plani", "Momira Gajića 7, 11320 Velika Plana", "026/522-205"),
        Entry("Osnovni sud u Velikom Gradištu", "Žitni Trg bb, 12220 Veliko Gradište", "012/662-151"),
        Entry("Osnovni sud u Vranju", "Kralja Milana 2, 17501 Vranje", "017/423-990"),
        Entry("Osnovni sud u Vrbasu", "Palih boraca 9/c, 21460 Vrbas", "021/704-769"),
        Entry("Osnovni sud u Vršcu", "Žarka Zrenjanina 41-43, 26300 Vršac", "013/831-343"),
        Entry("Osnovni sud u Zaječaru", "Trg oslobođenja 30, 19000 Zaječar", "019/420-466"),
        Entry("Osnovni sud u Zrenjaninu", "Kej 2. oktobra 1, 23000 Zrenjanin", "023/564-737"),
        Entry("Osnovni sud u Čačku", "Cara Dušana 8/1, 32101 Čačak", "032/227-258"),
        Entry("Osnovni sud u Šapcu", "Karađorđeva 25, 15000 Šabac", "015/354-614"),
        Entry("Osnovni sud u Šidu", "Cara Dušana 4, 22240 Šid", "022/712-126"),

        // Privredni sudovi
        Entry("Privredni sud u Beogradu", "Masarikova 2, 11000 Beograd", "011/2060-117"),
        Entry("Privredni sud u Valjevu", "Karađorđeva 48a, 14000 Valjevo", "014/222-144"),
        Entry("Privredni sud u Zaječaru", "Trg oslobođenja 30, 19000 Zaječar", "019/442-072"),
        Entry("Privredni sud u Zrenjaninu", "Kej 2. oktobra 1, 23000 Zrenjanin", "023/561-929"),
        Entry("Privredni sud u Kraljevu", "Cara Dušana 41, 36000 Kraljevo", "036/312-519"),
        Entry("Privredni sud u Kragujevcu", "Ulica Slobode 4, 34000 Kragujevac", "034/619-6000"),
        Entry("Privredni sud u Leskovcu", "Bulevar Oslobođenja 2, 16000 Leskovac", "016/230-502"),
        Entry("Privredni sud u Nišu", "Vojvode Putnika 2b, 18000 Niš", "018/520-451"),
        Entry("Privredni sud u Novom Sadu", "Sutjeska 3, 21000 Novi Sad", "021/4876-201"),
        Entry("Privredni sud u Pančevu", "Vojvode Radomira Putnika 13-15, 26000 Pančevo", "013/344-366"),
        Entry("Privredni sud u Požarevcu", "Jovana Šerbanovića 4, 12000 Požarevac", "012/521-652"),
        Entry("Privredni sud u Somboru", "Blagojevića 1, 25101 Sombor", "025/415-822"),
        Entry("Privredni sud u Sremskoj Mitrovici", "Trg Svetog Dimitrija 39, 22000 Sremska Mitrovica", "022/600-182"),
        Entry("Privredni sud u Subotici", "Senćanski put 1, 24000 Subotica", "024/524-861"),
        Entry("Privredni sud u Užicu", "Marije Mage Magazinović 11, 31000 Užice", "031/513-931"),
        Entry("Privredni sud u Čačku", "Cara Dušana 6, 32000 Čačak", "032/327-251"),

        // Prekršajni apelacioni sud i odeljenja
        Entry("Prekršajni apelacioni sud", "Katanićeva 15, 11000 Beograd", "011/635-2900"),
        Entry("Prekršajni apelacioni sud - Odeljenje u Kragujevcu", "Ulica Slobode 4, 34000 Kragujevac", "034/619-6451"),
        Entry("Prekršajni apelacioni sud - Odeljenje u Nišu", "Vojvode Putnika bb, 18000 Niš", "018/513-663"),
        Entry("Prekršajni apelacioni sud - Odeljenje u Novom Sadu", "Bulevar Oslobođenja 58, 21000 Novi Sad", "021/4896-185"),

        // Prekršajni sudovi
        Entry("Prekršajni sud u Aranđelovcu", "Knjaza Miloša 102, 34300 Aranđelovac", "034/723-908"),
        Entry("Prekršajni sud u Bačkoj Palanci", "Kralja Petra I 18, 21400 Bačka Palanka", "021/751-098"),
        Entry("Prekršajni sud u Beogradu", "Ustanička 14, 11000 Beograd", "011/655-0804"),
        Entry("Prekršajni sud u Bečeju", "Danila Kiša 8, 21220 Bečej", "021/6912-329"),
        Entry("Prekršajni sud u Gornjem Milanovcu", "Kneza Aleksandra 29, 32300 Gornji Milanovac", "032/711-245"),
        Entry("Prekršajni sud u Jagodini", "Kneginje Milice 15, 35000 Jagodina", "035/245-254"),
        Entry("Prekršajni sud u Kikindi", "Svetozara Miletića 1, 23300 Kikinda", "0230/421-015"),
        Entry("Prekršajni sud u Kragujevcu", "Trg Slobode 3, 34000 Kragujevac", "034/306-157"),
        Entry("Prekršajni sud u Kraljevu", "Trg Jovana Sarića 1, 36000 Kraljevo", "036/321-591"),
        Entry("Prekršajni sud u Kruševcu", "Stevana Sinđelića 1, 37000 Kruševac", "037/423-723"),
        Entry("Prekršajni sud u Lazarevcu", "Karađorđeva 19, 11550 Lazarevac", "011/812-0636"),
        Entry("Prekršajni sud u Leskovcu", "Pana Đukića 18, 16000 Leskovac", "016/213-304"),
        Entry("Prekršajni sud u Loznici", "Jovana Cvijića bb, 15300 Loznica", "015/882-305"),
        Entry("Prekršajni sud u Mladenovcu", "Kralja Aleksandra Obrenovića 76, 11400 Mladenovac", "011/8230-240"),
        Entry("Prekršajni sud u Negotinu", "Kraljevića Marka 2, 19300 Negotin", "019/542-864"),
        Entry("Prekršajni sud u Nišu", "Vojvode Putnika 26, 18000 Niš", "018/415-6802"),
        Entry("Prekršajni sud u Novom Pazaru", "Žitni trg 23, 36300 Novi Pazar", "020/350-155"),
        Entry("Prekršajni sud u Novom Sadu", "Bulevar oslobođenja 58, 21000 Novi Sad", "021/4896-100"),
        Entry("Prekršajni sud u Obrenovcu", "Aleksandra Ace Simovića 9a, 11500 Obrenovac", "011/8721-100"),
        Entry("Prekršajni sud u Pančevu", "Stevana Šupljikca 33, 26000 Pančevo", "013/215-6360"),
        Entry("Prekršajni sud u Paraćinu", "Tome Živanovića 10, 35250 Paraćin", "035/569-690"),
        Entry("Prekršajni sud u Pirotu", "Srpskih vladara 126, 18300 Pirot", "010/320-412"),
        Entry("Prekršajni sud u Požarevcu", "Porečka 2a, 12000 Požarevac", "012/532-343"),
        Entry("Prekršajni sud u Požegi", "Uče Dimitrijevića 6, 31210 Požega", "031/811-254"),
        Entry("Prekršajni sud u Preševu", "Maršala Tita bb, 17523 Preševo", "017/668-966"),
        Entry("Prekršajni sud u Prijepolju", "Valterova 173, 31300 Prijepolje", "033/712-355"),
        Entry("Prekršajni sud u Prokuplju", "Tatkova 1, 18400 Prokuplje", "027/324-215"),
        Entry("Prekršajni sud u Raški", "Miluna Ivanovića bb, 36350 Raška", "036/736-243"),
        Entry("Prekršajni sud u Rumi", "Železnička 13, 22400 Ruma", "022/474-324"),
        Entry("Prekršajni sud u Senti", "Glavni trg 2, 24400 Senta", "024/811-034"),
        Entry("Prekršajni sud u Sjenici", "Ahmeta Abdagića bb, 36310 Sjenica", "020/5741-099"),
        Entry("Prekršajni sud u Smederevu", "Omladinska 1, 11300 Smederevo", "026/4627-134"),
        Entry("Prekršajni sud u Somboru", "Trg Cara Uroša 1, 25101 Sombor", "025/421-494"),
        Entry("Prekršajni sud u Sremskoj Mitrovici", "Trg Svetog Dimitrija 39, 22000 Sremska Mitrovica", "022/600-185"),
        Entry("Prekršajni sud u Subotici", "Trg Lazara Nešića 1, 24000 Subotica", "024/641-167"),
        Entry("Prekršajni sud u Trsteniku", "Doktora Milunovića bb, 37240 Trstenik", "037/712-195"),
        Entry("Prekršajni sud u Užicu", "Marije Mage Magazinović 11, 31000 Užice", "031/315-0086"),
        Entry("Prekršajni sud u Valjevu", "Vuka Karadžića 5, 14000 Valjevo", "014/295-707"),
        Entry("Prekršajni sud u Vranju", "Zadarska 2, 17501 Vranje", "017/421-275"),
        Entry("Prekršajni sud u Vršcu", "Vaska Pope 7, 26300 Vršac", "013/836-433"),
        Entry("Prekršajni sud u Zaječaru", "Generala Gambete 44/4, 19000 Zaječar", "019/422-734"),
        Entry("Prekršajni sud u Zrenjaninu", "Žitni trg bb, 23000 Zrenjanin", "023/525-253"),
        Entry("Prekršajni sud u Čačku", "Kralja Petra I bb, 32000 Čačak", "031/315-0086"),
        Entry("Prekršajni sud u Šapcu", "Pop Lukina 2, 15000 Šabac", "015/314-355")
    )

    fun defaultCourts(): List<Court> {
        val now = Instant.now()
        return entries.map { e ->
            Court(
                naziv = e.naziv,
                adresa = e.adresa,
                telefon = e.telefon,
                napomena = e.napomena,
                datumKreiranja = now,
                datumIzmene = now
            )
        }
    }
}
