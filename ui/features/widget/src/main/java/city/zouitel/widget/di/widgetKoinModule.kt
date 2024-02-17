package city.zouitel.widget.di

import city.zouitel.widget.viewmodel.WidgetViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val widgetKoinModule = module {
    viewModelOf(::WidgetViewModel)
}