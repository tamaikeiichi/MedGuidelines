package com.example.medguidelines.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(id = R.string.childPughTitle)) }
            )
        },
        bottomBar = {
            BottomAppBar (
                modifier = Modifier
                    .padding(10.dp),

            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "child A or B"
                    )
                    Text(
                        text = "child A or B"
                    )
                }

            }
        }
    )
    { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            val bilirubinScore = childPughButtonAndScore(bilirubinGrade, stringResource(id = R.string.bilirubinTitle))
            val albuminScore = childPughButtonAndScore(albuminGrade, stringResource(id = R.string.albuminTitle))
            val ptScore = childPughButtonAndScore(ptGrade, stringResource(id = R.string.ptTitle))
            val ascitesScore = childPughButtonAndScore(ascitesGrade, stringResource(id = R.string.ascitesTitle))
            val encephalopathyScore = childPughButtonAndScore(encephalopathyGrade, stringResource(id = R.string.encephalopaphyTitle))

            val totalScore = bilirubinScore + albuminScore + ptScore + ascitesScore + encephalopathyScore

            Text(text=totalScore.toString())
            val childPughScoreABC = when (totalScore) {
                in 5..6 -> "A"
                in 7..9 -> "B"
                else -> "C"
            }
            Text(text = childPughScoreABC)
        }
    }

}

@Composable
fun childPughButtonAndScore(
    factor : List<labDataNames>,
    title : String,
): Int
{
    val radioOptions : List<labDataNames> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    ThreeRadioButton(factor, selectedOption, onOptionSelected, title)

    val score =
        if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[0].stringid)) 1
        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[1].stringid)) 2
        else  3

    return score
}


@Composable
fun ThreeRadioButton(radioOptions: List<labDataNames>,
                     selectedOption: labDataNames,
                     onOptionSelected : (selectedOption: labDataNames ) -> Unit,
                     title : String
){
    Column(){
        Text(text = title,
            Modifier
                .padding(10.dp))
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

}