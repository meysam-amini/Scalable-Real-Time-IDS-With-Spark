import {
  Component,
  OnInit,
  NgZone,
  ElementRef,
  NgModule,
  ComponentRef,
  ViewChild,
  ViewContainerRef, ComponentFactoryResolver, Injector
} from '@angular/core';
import {ChartService} from './services/chart.service';
import {Chart, ChartType,ChartDataSets,ChartOptions} from 'chart.js';
import { BaseChartDirective, Label, Color } from 'ng2-charts';
import {LineChartComponent} from "./components/charts/line-chart/line-chart.component";
import {BaseChartComponent} from "./components/charts/base-chart/base-chart.component";
import {ThemeService} from "./theme/theme.service";
import {dark, light, Theme} from "./theme/theme";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  entryComponents: [LineChartComponent]
})
export class AppComponent implements OnInit{
  title = 'dashboard';


  constructor(public chartService: ChartService,
              public themeService:ThemeService) {

  }


  //data we read from api
  /*data_groups:Array<any>=[];
  group_count:Array<any>=[];*/

  //map of traffic types:
  /*traffic_group_map = new Map()
  .set('normal', 0)
  .set('syn_flooding', 1)
  .set('ack_flooding', 2)
  .set('host_bruteforce', 3)
  .set('http_flooding', 4)
  .set('udp_flooding', 5)
  .set('arp_spoofing', 6)
  .set('port_scanning', 7)
  .set('scan_os', 8)
  .set('scan_host_discovery', 9);*/
  // @ViewChild('Mycontainer', { read: ViewContainerRef }) entry: ViewContainerRef;
  // constructor(private chartService: ChartService,
  //             private resolver: ComponentFactoryResolver){

  // }




  ngOnInit(): void {
    this.themeService.setActiveTheme(this.themeService.getActiveTheme());
  }



}
