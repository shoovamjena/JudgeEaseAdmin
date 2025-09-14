package com.example.judgeeaseadmin.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.judgeeaseadmin.dialogs.CompetitionDialog
import com.example.judgeeaseadmin.viewmodel.AppViewModel

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    authViewModel: AppViewModel
) {
    val competitions by authViewModel.competitions.collectAsState()
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        CompetitionDialog(
            onDismiss = { showDialog.value = false },
            onCreate = { title, desc, venue, organizer, startDateTime, endDateTime ->
                authViewModel.createCompetition(title, desc, venue, organizer, startDateTime, endDateTime)
                showDialog.value = false
            }
        )
    }

    Column(
        modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome Admin!", fontSize = 28.sp)

        Spacer(Modifier.height(12.dp))

        Button(onClick = { showDialog.value = true }) {
            Text("Create Competition")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(competitions) { comp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("competition/${comp.id}") }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(comp.name, fontSize = 20.sp)
                        Text(comp.description, fontSize = 14.sp)
                        Text(comp.venue, fontSize = 14.sp)
                        Text(comp.organizer, fontSize = 14.sp)
                        Text(comp.startDateTime.toString(), fontSize = 14.sp)
                        Text(comp.endDateTime.toString(), fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = { authViewModel.signOut() }) {
            Text("SIGN OUT")
        }
    }
}
