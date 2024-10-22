package com.demo.mvitodoapp

import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demo.mvitodoapp.intent.TodoIntent
import com.demo.mvitodoapp.model.local.Todo
import com.demo.mvitodoapp.model.repository.TodoRepository
import com.demo.mvitodoapp.ui.theme.MviTodoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: TodoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MviTodoAppTheme {
                Surface(
                    Modifier.fillMaxSize()
                ) {
                    val list by repository.getAllTodoList().collectAsState(initial = emptyList())
                    val scope = rememberCoroutineScope()
                    MainScreen(list = list) { intent ->
                        when (intent) {
                            is TodoIntent.Delete -> scope.launch {
                                repository.delete(intent.todo)
                            }

                            is TodoIntent.Insert -> scope.launch {
                                repository.insert(intent.todo)
                            }

                            is TodoIntent.Update -> scope.launch {
                                repository.update(intent.todo)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(list: List<Todo>, onIntent: (TodoIntent) -> Unit) {

    val title = remember {
        mutableStateOf("")
    }

    Scaffold {innerpadding ->
        Column(modifier = Modifier
            .padding(innerpadding)
            .fillMaxSize()) {
            if(list.isEmpty()) {
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(innerpadding),
                    contentAlignment = Alignment.Center) {
                    Text(text = "Nothing Found")
                }
            } else {
                LazyColumn(modifier = Modifier
                    .weight(1f)
                    .padding(innerpadding)) {
                    items(list) {
                        val isChecked = remember {
                            mutableStateOf(it.isDone)
                        }
                        Column(modifier = Modifier
                            .combinedClickable(enabled = true, onClick = {}, onLongClick = {
                                onIntent.invoke(TodoIntent.Delete(it))
                            })
                            .fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = it.title)
                                Checkbox(checked = isChecked.value, onCheckedChange = {check ->
                                    isChecked.value = check
                                    onIntent.invoke(TodoIntent.Update(it.copy(isDone = isChecked.value)))
                                })
                            }
                            Divider()
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                
                TextField(value = title.value, onValueChange = {
                    title.value = it
                }, modifier = Modifier.fillMaxWidth())
                Button(onClick = {
                    onIntent.invoke(
                        TodoIntent.Insert(Todo(title = title.value, isDone = false, id = 0))
                    )
                    title.value = ""
                }
                ) {
                    Text(text = "Save Todo")
                }
            }
        }


    }
}

