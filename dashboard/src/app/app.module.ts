import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {ChartService} from './services/chart.service';
import {HttpClientModule} from "@angular/common/http";
import {ChartsModule} from "ng2-charts";
import { RadarChartComponent } from './components/charts/radar-chart/radar-chart.component';
import { LineChartComponent } from './components/charts/line-chart/line-chart.component';
import { BaseChartComponent } from './components/charts/base-chart/base-chart.component';
import {RouterModule, Routes} from "@angular/router";
import { PageContainerComponent } from './components/page-container/page-container.component';
import { DataTableComponent } from './components/data-table/data-table.component';
import {DataTablesModule} from "angular-datatables";
import {ApiService} from "./services/api.service";
import {NgbModal, NgbModule} from "@ng-bootstrap/ng-bootstrap";
import { DialogApisComponent } from './components/dialog-apis/dialog-apis.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatInputModule} from "@angular/material/input";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatListModule} from "@angular/material/list";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatDividerModule} from "@angular/material/divider";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {ReactiveFormsModule} from "@angular/forms";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import { HeaderComponent } from './components/header/header.component';
import {MatTab, MatTabGroup, MatTabsModule} from "@angular/material/tabs";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatSortModule} from "@angular/material/sort";
import {MatTableModule} from "@angular/material/table";


const materialModules = [
  MatToolbarModule,
  MatIconModule,
  MatButtonModule,
  MatCardModule,
  MatListModule,
  MatGridListModule,
  MatDividerModule,
  MatFormFieldModule,
  MatInputModule,
  MatSelectModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatTabsModule,
  MatTableModule,
  MatPaginatorModule,
  MatSortModule,
  MatProgressSpinnerModule,
];

const routes: Routes = [
  {path: ':keyword', component: PageContainerComponent},
  // {path: 'line_chart', component: LineChartComponent},
  // {path: 'radar_chart', component: RadarChartComponent},
  // {path: 'data_table', component: RadarChartComponent},
  {path: '', redirectTo: '/line_charts', pathMatch: 'full'},
  {path: '**', redirectTo: '/line_charts', pathMatch: 'full'}
];

@NgModule({
  declarations: [
    AppComponent,
    RadarChartComponent,
    LineChartComponent,
    BaseChartComponent,
    PageContainerComponent,
    DataTableComponent,
    DialogApisComponent,
    HeaderComponent,
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ChartsModule,
    DataTablesModule,
    NgbModule,
    BrowserAnimationsModule,
    ...materialModules,
    ReactiveFormsModule,
  ],
  exports:[...materialModules],
  providers: [ChartService,ApiService],
  bootstrap: [AppComponent]
})
export class AppModule { }
