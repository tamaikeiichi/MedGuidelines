package com.example.medguidelines.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medguidelines.R
import com.example.medguidelines.data.albuminGrade
import com.example.medguidelines.data.ascitesGrade
import com.example.medguidelines.data.bilirubinGrade
import com.example.medguidelines.data.encephalopathyGrade
import com.example.medguidelines.data.labDataNames
import com.example.medguidelines.data.ptGrade


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildPughScreen() {
    var totalScore by remember { mutableStateOf(0) }
    var childPughScoreABC by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(text = stringResource(id = R.string.childPughTitle)) }
            )
        },
        bottomBar = {
            BottomAppBar (){
                Text(
                    text = "Child-Pugh $childPughScoreABC ($totalScore)",
                    fontSize = 20.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            totalScore = childPughTotalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            childPughScoreABC = when (totalScore) {
                in 5..6 -> "A"
                in 7..9 -> "B"
                else -> "C"
            }
        }
    }
}

@Composable
fun childPughTotalScore(): Int {
    Column (
        Modifier
        //.verticalScroll(rememberScrollState()),
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
    }
    val bilirubinScore = childPughButtonAndScore(
        bilirubinGrade,
        stringResource(id = R.string.bilirubinTitle),
        stringResource(id = R.string.bilirubinTitleNote)
    )
    val albuminScore =
        childPughButtonAndScore(albuminGrade, stringResource(id = R.string.albuminTitle),
            stringResource(id = R.string.albuminTitleNote))
    val ptScore = childPughButtonAndScore(ptGrade, stringResource(id = R.string.ptTitle),stringResource(id = R.string.ptTitleNote))
    val ascitesScore =
        childPughButtonAndScore(ascitesGrade, stringResource(id = R.string.ascitesTitle), stringResource(id = R.string.ascitesTitleNote))
    val encephalopathyScore = childPughButtonAndScore(
        encephalopathyGrade,
        stringResource(id = R.string.encephalopaphyTitle),stringResource(id = R.string.encephalopaphyTitleNote)
    )

    val totalScore =
        bilirubinScore + albuminScore + ptScore + ascitesScore + encephalopathyScore
    //Text(text = totalScore.toString())
    return  totalScore
}

data class TotalScoreClass (
    val totalScoreValue : Int
)

@Composable
fun childPughButtonAndScore(
    factor : List<labDataNames>,
    title : String,
    titleNote : String
): Int
{
    val radioOptions : List<labDataNames> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    ThreeRadioButton(factor, selectedOption, onOptionSelected, title, titleNote)

    val score: Int =
        if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[0].stringid)) 1
        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[1].stringid)) 2
        else  3

    return score
}


@Composable
fun ThreeRadioButton(radioOptions: List<labDataNames>,
                     selectedOption: labDataNames,
                     onOptionSelected : (selectedOption: labDataNames ) -> Unit,
                     title : String,
                     titleNote : String,
){
    Column(){
        Card(
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ){
            var expanded by remember { mutableStateOf(false)}
            Row(
                modifier = Modifier.padding(2.dp)
                    .animateContentSize ()
            ){
                Column (
                    modifier = Modifier.weight(1f).padding(5.dp)
                ){
                    Text(text = title,
                        Modifier
                            .padding(10.dp))
                    if (expanded) {
                        Text (text = titleNote,
                            Modifier
                                .padding(2.dp))
                    }
                }
                IconButton(onClick = { expanded = !expanded}) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown,

                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
        }

    }

    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Row(
        Modifier
            .selectableGroup()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        radioOptions.forEach { text ->
            Row(
                Modifier//.fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = stringResource(id = text.stringid),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp),
                    softWrap = true,
                )
            }
        }
    }
}


@Preview
@Composable
fun ChildPughScreenPreview(){
    ChildPughScreen()
}