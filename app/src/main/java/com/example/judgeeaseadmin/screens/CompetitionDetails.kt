package com.example.judgeeaseadmin.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.judgeeaseadmin.viewmodel.AppViewModel

@Composable
fun CompetitionDetailScreen(
    compId: String,
    authViewModel: AppViewModel
) {
    val compFlow = remember { authViewModel.repository.getCompetitionById(compId) }
    val comp by compFlow.collectAsState(initial = null)

    var teamName by remember { mutableStateOf("") }

    if (comp == null) {
        Text("Loading competition...")
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text(comp!!.name, fontSize = 26.sp)
            Text(comp!!.description, fontSize = 16.sp)

            Spacer(Modifier.height(20.dp))
            Text("Teams:", fontSize = 18.sp)
//            comp!!.teams.forEach { team ->
//                Text("• $team", fontSize = 14.sp)
//            }

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = teamName,
                onValueChange = { teamName = it },
                label = { Text("Add Team") }
            )
            Button(onClick = {
                authViewModel.addTeamToCompetition(comp!!.id, teamName)
                teamName = ""
            }) {
                Text("Add Team")
            }
        }
    }
}
