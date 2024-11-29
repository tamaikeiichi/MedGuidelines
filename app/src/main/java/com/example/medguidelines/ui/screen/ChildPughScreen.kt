package com.example.medguidelines.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import com.example.medguidelines.data.ascitesgrade
import com.example.medguidelines.data.encephalopathygrade
import com.example.medguidelines.data.labDataNames


@Composable
fun ChildPughScreen() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = stringResource(id = R.string.index1),
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
        )
        val radioOptionsAscitesgrade : List<labDataNames> = ascitesgrade
        val (selectedOptionAscitesgrade, onOptionSelectedAscitesgrade) = remember { mutableStateOf(radioOptionsAscitesgrade[0]) }
        ThreeRadioButton(ascitesgrade, selectedOptionAscitesgrade, onOptionSelectedAscitesgrade)

        val ascitesgradeScore =
            if (stringResource(id = selectedOptionAscitesgrade.stringid) == "absent") 1
            else if (stringResource(id = selectedOptionAscitesgrade.stringid) == "slight") 2
            else  3

        val radioOptionsEncephalopathygrade : List<labDataNames> = encephalopathygrade
        val (selectedOptionEncephalopathygrade, onOptionSelectedEncephalopathygrade) = remember { mutableStateOf(radioOptionsEncephalopathygrade[0]) }
        ThreeRadioButton(encephalopathygrade, selectedOptionEncephalopathygrade, onOptionSelectedEncephalopathygrade)

        val encephalopathygradeScore =
            if (stringResource(id = selectedOptionEncephalopathygrade.stringid) == "none") 1
            else if (stringResource(id = selectedOptionEncephalopathygrade.stringid) == "grade 1 or 2") 2
            else  3

        val totalScore = ascitesgradeScore + encephalopathygradeScore

        Text(text=totalScore.toString())

    }
    }

@Composable
fun ThreeRadioButton(radioOptions: List<labDataNames>,
                     selectedOption: labDataNames,
                     onOptionSelected : (selectedOption: labDataNames ) -> Unit,
    ){
// Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Row(Modifier.selectableGroup()) {
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
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}