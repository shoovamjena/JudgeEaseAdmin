package com.example.judgeeaseadmin.screens

// In package com.example.judgeeaseadmin.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.judgeeaseadmin.model.Teams
import com.example.judgeeaseadmin.viewmodel.AppViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CompetitionDetailScreen(
    compId: String,
    authViewModel: AppViewModel,
    navController: NavController
) {
    // Assuming competitionRepository.getCompetitionById now returns a Flow of Competition with List<Team>
    val compFlow = remember { authViewModel.competitionRepository.getCompetitionById(compId) }
    val comp by compFlow.collectAsState(initial = null)

    var showAddTeamDialog by remember { mutableStateOf(false) }

    val currentComp = comp

    if (comp == null) {
        Text("Loading...")
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (currentComp != null) {
                Text(currentComp.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }else{
                Text("No teams found")
            }
            Spacer(Modifier.height(8.dp))
            if (currentComp != null) {
                Text(currentComp.description, fontSize = 16.sp)
            }else{
                Text("No teams found")
            }
            Spacer(Modifier.height(24.dp))

            // Button to open the "Add Team" dialog
            Button(onClick = { showAddTeamDialog = true }) {
                Text("Add New Team")
            }

            Spacer(Modifier.height(16.dp))
            Text("Teams:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            // Display teams in a LazyColumn
            if(currentComp != null) {
                LazyColumn(
                    modifier = Modifier.weight(1f), // Takes remaining space
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentComp.teams) { team ->
                        TeamItem(team = team)
                    }

                }
            }else{
                Text("No teams found")
            }

            Spacer(Modifier.height(16.dp))
            // Delete Competition Button
            Button(
                onClick = {
                    authViewModel.deleteCompetition(compId)
                    navController.popBackStack("home", inclusive = false)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Competition")
            }
        }
    }

    // Composable Dialog is shown or hidden based on the state
    if (showAddTeamDialog) {
        AddTeamDialog(
            onDismiss = { showAddTeamDialog = false },
            onConfirm = { newTeam ->
                // Add necessary details before sending to ViewModel
                val completeTeam = newTeam.copy(
                    eventId = compId,
                    adminId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("Admin not found")
                )
                authViewModel.addTeam(compId, completeTeam)
                showAddTeamDialog = false
            }
        )
    }
}

/**
 * An expandable list item to display team details.
 */
@Composable
fun TeamItem(team: Teams) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Always visible part
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = team.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Text(text = "Leader: ${team.leader}", fontSize = 14.sp)
            }

            // Expandable part with animation
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Problem Statement:", fontWeight = FontWeight.SemiBold)
                    Text(team.problemStatement)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Members:", fontWeight = FontWeight.SemiBold)
                    team.members.forEach { member ->
                        Text("• $member")
                    }
                }
            }
        }
    }
}

/**
 * A dialog to input all the details for a new team.
 */
@Composable
fun AddTeamDialog(
    onDismiss: () -> Unit,
    onConfirm: (Teams) -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    var teamLeader by remember { mutableStateOf("") }
    var teamMembers by remember { mutableStateOf("") }
    var problemStatement by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Team") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team Name") }
                )
                OutlinedTextField(
                    value = teamLeader,
                    onValueChange = { teamLeader = it },
                    label = { Text("Team Leader") }
                )
                OutlinedTextField(
                    value = problemStatement,
                    onValueChange = { problemStatement = it },
                    label = { Text("Problem Statement") }
                )
                OutlinedTextField(
                    value = teamMembers,
                    onValueChange = { teamMembers = it },
                    label = { Text("Members") },
                    placeholder = { Text("John, Jane, Doe (comma-separated)")}
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val membersList = teamMembers.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    val newTeam = Teams(
                        name = teamName,
                        leader = teamLeader,
                        problemStatement = problemStatement,
                        members = membersList
                    )
                    onConfirm(newTeam)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}