import {Component, AfterViewInit, ElementRef, NgZone, OnInit, ViewChild} from '@angular/core';
import {Chart, ChartDataSets, ChartOptions, ChartPluginsOptions, ChartType} from "chart.js";
import {ChartService} from "../../../services/chart.service";
import {Color, Label} from "ng2-charts";
import {ThemeService} from "../../../theme/theme.service";
import {light, Theme} from "../../../theme/theme";
import {MonitoringZone} from "../../../model/monitoring-zone";

@Component({
  selector: 'app-base-chart',
  templateUrl: './base-chart.component.html',
  styleUrls: ['./base-chart.component.css']
})
export class BaseChartComponent implements OnInit, AfterViewInit {
  @ViewChild("mychart") canvas: ElementRef;


  //chart properties
  chartDataSets: ChartDataSets[] = [];
  chartLabels: Label[] = [];
  chartOptions: ChartOptions = {};
  chartLegend = true;
  chartType: ChartType = 'line';
  chartColors: Color[] = [];
  /////////////////////////////////////
  chartDataArray: Array<any> = [[]];
  dataLables:Array<string>=[]


  // sumOfEachGroupPerMinut: Array<any> = [];
  groupColors: Array<string> = [
    // '#00ff06',
    // '#640201',
    // '#8b0100',
    // '#bd0301',
    // '#d50301',
    // '#e70301',
    // '#fd0401',
    // '#ff440c',
    // '#ff8010',
    // '#ffd90e',

  ]


  /*{
    borderColor: "#d50301",
    backgroundColor: "#d50301",
  },
  {
    borderColor: "#fffc26",
    backgroundColor: "#fffc26",
  },
];*/

 // zones: Array<MonitoringZone> = [];


  attack_types: Array<string> = ["normal", "syn_flooding", "ack_flooding",
    "host_bruteforce", "http_flooding", "udp_flooding",
    "arp_spoofing", "port_scanning", "scan_os",
    "scan_host_discovery"];
  //public minute_index: number = 0


  constructor(public chartService: ChartService,
              public elmref: ElementRef,
              public themeService: ThemeService) {

    this.canvas = elmref;
  }


  ngOnInit(): void {

   this.initChart();

    this.updateChartThemes()

    //this.minute_index = 0;


    // this.setUpdateInterval(1000);

    /*this.chartService.getSSE('http://localhost:8080/NetFlow_R')
      .subscribe(event=> {
        let data = JSON.parse(event.data);
        this.pushData(data);
        //   console.log(data.prediction);
      });*/
  }

  /* private getRandomRgb() {
     var num = Math.round(0xffffff * Math.random());
     var r = num >> 40;
     var g = num >> 8 & 255;
     var b = num & 255;
     return 'rgb(' + r + ', ' + g + ', ' + b + ','+0.7+')';
   }*/

  gradient: Array<any> = []

  ngAfterViewInit(): void {

    for (let index = 0; index < this.dataLables.length; index++) {

      this.gradient[index] = this.canvas.nativeElement.getContext('2d').createLinearGradient(0, 10, 0, 400);

      this.gradient[index].addColorStop(0, this.groupColors[index]);
      this.gradient[index].addColorStop(1, 'rgb(0,0,0,0.5)');

      // this.gradient[index].addColorStop(1, this.getRandomRgb());

      this.chartColors.push({
          backgroundColor: this.gradient[index],
          borderColor: this.gradient[index],
          pointBackgroundColor: this.gradient[index]
        }
      )
    }

    this.updateChartOptions(this.themeService.getActiveTheme());
  }


  public updateChartThemes() {
    // subscribe to theme changing
    this.themeService.currentTheme.subscribe(
      theme => this.updateChartOptions(theme)
    );
  }


  public initChart() {

    console.log('setting labels initChart: ',this.dataLables)
    this.createLabels();

    this.chartDataSets = [];
    // this.chartLabels= [];
    // this.chartOptions = {};
    // this.chartColors = [];

    for (let index = 0; index < this.dataLables.length; index++) {
      this.chartDataSets[index] = {};
      this.chartDataArray[index] = Array(this.chartLabels.length).fill(0);
      this.chartDataSets[index].data = [];

      this.chartDataSets[index].label = this.dataLables[index];
      this.chartDataSets[index].pointRadius = -1;
      this.chartDataSets[index].borderWidth = 1.5;
    }

    this.updateChartOptions(this.themeService.getActiveTheme());

  }

  public setUpdateInterval(interval:number){

    // interval=200;
    setInterval(() => {

     // this.minute_index++;
      //   console.log("update");
      this.updateChart()
    }, interval);

    // setInterval(() => {
    //   let a = Math.floor(Math.random() * this.num_groups)
    //   this.pushData(a);
    //   //   console.log(this.minute_index);
    // }, 100);

  }



  public createLabels() {
  }

  public pushData(zone: number,attackType:number) {
    /* Implemented in Child Classes*/
  }

  public updateChart(): void {
    /* Implemented in Child Classes*/
  }


  public updateChartOptions(theme: Theme) {

    /* Implemented in Child Classes*/

  }

}
