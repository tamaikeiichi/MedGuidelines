package com.example.medguidelines.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medguidelines.R
import com.example.medguidelines.data.IndexNames
import java.security.MessageDigest

@Composable
fun ListItem (name: String, onClick: () -> Unit){
    Column {
        Card {
            Text(text = name,
                fontSize = 25.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable { onClick() }
            )
        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}

//data class ListItemData (val name: String, val onClick: () -> Unit)
//val listItemData = ListItemData()