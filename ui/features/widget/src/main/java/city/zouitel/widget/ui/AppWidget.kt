package city.zouitel.widget.ui

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import city.zouitel.domain.model.Note
import city.zouitel.domain.usecase.WidgetUseCase
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppWidget: GlanceAppWidget(), KoinComponent {

    private val getAll: WidgetUseCase.GetAllWidgetMainEntityById by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) = provideContent {
//        val viewModel = koinViewModel<WidgetViewModel>()
//        val ctx = LocalContext.current.applicationContext

//        val viewModel = EntryPoints.get(
//            ctx,
//            EntryPoint::class.java
//        ).viewmodel()

        val getAllNotes by getAll().collectAsState(initial = null)

        WidgetListNotes(notes = getAllNotes, context = context)
    }

    @Composable
    fun WidgetListNotes(
        notes: List<Note>?,
        context: Context,
        widgetViewModel: WidgetViewModel = koinViewModel(),
    ) {
        val getAllNotesById by remember(widgetViewModel, widgetViewModel::allNotesById).collectAsState()

        LazyColumn(
            modifier = GlanceModifier
        ) {
            items(
                items = notes ?: emptyList()
            ) { entity ->
                Column {
                    Row(
                        modifier = GlanceModifier
                            .background(ColorProvider(Color(entity.dataEntity.color)))
                            .fillMaxWidth()
                            .cornerRadius(15.dp)
                    ) {
                        kotlin.runCatching {
                            Image(
                                ImageProvider(
                                    bitmap = widgetViewModel::imageDecoder.invoke(context, entity.dataEntity.uid)
                                ),
                                null,
                                modifier = GlanceModifier
                                    .cornerRadius(15.dp)
                            )
                        }

                        Spacer(GlanceModifier.width(15.dp))

                        Column {
                            Text(
                                text = entity.dataEntity.title ?: "",
                                style = androidx.glance.text.TextStyle(
                                    fontSize = 19.sp,
                                    color = ColorProvider(Color(entity.dataEntity.textColor))
                                ),
                                modifier = GlanceModifier
                                    .padding(3.dp)
                            )

                            Text(
                                text = entity.dataEntity.description ?: "",
                                style = androidx.glance.text.TextStyle(
                                    fontSize = 15.sp,
                                    color = ColorProvider(Color(entity.dataEntity.textColor))
                                ),
                                modifier = GlanceModifier
                                    .padding(
                                        start = 3.dp,
                                        end = 3.dp,
                                        bottom = 7.dp
                                    )
                            )
                        }

                    }

                    Spacer(GlanceModifier.height(10.dp))
                }
            }
        }
    }
}