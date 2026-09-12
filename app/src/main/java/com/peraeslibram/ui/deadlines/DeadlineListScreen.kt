package com.peraeslibram.ui.deadlines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.peraeslibram.ui.common.DeadlineCard
import com.peraeslibram.ui.common.EmptyState
import com.peraeslibram.ui.common.TimeframeHeader
import com.peraeslibram.ui.common.groupByTimeframe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlineListScreen(
    onOpenDrawer: () -> Unit,
    onOpenDeadline: (Long, Long) -> Unit,
    viewModel: DeadlineListViewModel = hiltViewModel()
) {
    val deadlines by viewModel.deadlines.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rokovi") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Meni")
                    }
                }
            )
        }
    ) { padding ->
        if (deadlines.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Default.HourglassBottom,
                    title = "Nema aktivnih rokova",
                    subtitle = "Rokovi koje dodate na predmetima pojaviće se ovde."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupByTimeframe(deadlines) { it.datum }.forEach { (label, items) ->
                    item { TimeframeHeader(label) }
                    items(items) { item ->
                        DeadlineCard(
                            deadline = item.deadline,
                            caseNaziv = item.caseNaziv,
                            onClick = { onOpenDeadline(item.deadline.caseId, item.deadline.id) }
                        )
                    }
                }
            }
        }
    }
}
