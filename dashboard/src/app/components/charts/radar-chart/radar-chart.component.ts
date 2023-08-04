import {Component, ElementRef, Input, OnInit} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType} from "chart.js";
import {ChartService} from "../../../services/chart.service";
import {BaseChartComponent} from "../base-chart/base-chart.component";
import {Label} from "ng2-charts";
import {ThemeService} from "../../../theme/theme.service";
import {Theme} from "../../../theme/theme";
import {MonitoringZone} from "../../../model/monitoring-zone";

@Component({
  selector: 'app-radar-chart',
  templateUrl: './radar-chart.component.html',
  styleUrls: ['./radar-chart.component.css']
})
export class RadarChartComponent extends BaseChartComponent implements OnInit {


  // @Input()
  zones: Array<string>=[];


  //chart properties
  chartType: ChartType = 'radar';
  //////////////////
  chartDataArray: Array<any> = [[]];
  // sumOfEachGroupPerMinut: Array<any> = [[]];

  private dataPipeReady: boolean = false;


  groupColors: Array<any> = [
    '#13d315',
    '#fd0401',
    '#fdf530',
    '#3029fd',
    '#fdfcf8',
    '#ff7c68',
    '#47fdec',
    '#fd9e10',
    '#fd33cd',
    '#951d20',
    '#097200',
  ];


  constructor(public chartService: ChartService,
              elmref: ElementRef,
              themeService: ThemeService) {

    super(chartService, elmref, themeService);
  }


  ngOnInit(): void {
    super.ngOnInit();
      this.setUpdateInterval(5000);
      this.chartService.ChartMonitoring.subscribe(zones => {

        console.log(' zones: ', zones)
        this.setZones(zones)
        this.startMonitor();
      })
  }

  ngAfterViewInit(): void {
    this.chartColors=[];
    super.ngAfterViewInit();
  }

  private setZones(apiList: Array<MonitoringZone>) {
    this.zones=[];
    for (let i = 0; i < apiList.length; i++) {
      this.zones.push(apiList[i].name);
    }
  }


  public setUpdateInterval(interval: number) {
    super.setUpdateInterval(interval);
  }

  private startMonitor() {

    this.setDataLables();
    this.initChart();
    this.ngAfterViewInit()
   this.updateChartOptions(this.themeService.getActiveTheme());

    for (let zoneIndex = 0; zoneIndex < this.zones.length; zoneIndex++) {
      this.chartService.ALL_APIs[zoneIndex].subscribe(data => {
        this.dataPipeReady = true;
        //Incrementing attack types count in each zone chart
        // this.chartDataArray[zoneIndex][JSON.parse(data.data).prediction]++;

        // console.log('data from ',,': ',JSON.parse(data.msg).prediction)

        // console.log("zones:"+this.zones+" zone: "+data.zone+" msg: "+JSON.parse(data.msg).prediction)
      this.pushData(this.zones.indexOf(data.zone), JSON.parse(data.msg).prediction);
      this.dataPipeReady=true;
      })
    }

  }

  public pushData(zoneIndex: number, attackType: number) {

   // console.log('zone:'+zoneIndex+" data:"+attackType)
    //increment group count in current time window(minute_index)
    this.chartDataArray[zoneIndex][attackType]++;

  }


  public updateChart(): void {
    //super.updateChart();
    // console.log('updating... : ', this.zones.length);
    if (this.dataPipeReady) {
      for (let zoneIndex = 0; zoneIndex < this.zones.length; zoneIndex++) {
        this.chartDataSets[zoneIndex].data = this.chartDataArray[zoneIndex];
        // console.log('radar chart: ', index, ' : ', this.chartDataSets[index].data);
      }
      //update chart
      this.chartDataSets = this.chartDataSets.slice();
    }
  }

  private setDataLables(){
    this.dataLables=[];
    for(let i=0;i<this.zones.length;i++){
      this.dataLables[i]=this.zones[i];
    }

    // console.log('zones from radar:',this.dataLables)

  }

  public createLabels() {
    this.chartLabels = ["normal", "syn_flooding", "ack_flooding",
      "host_bruteforce", "http_flooding", "udp_flooding",
      "arp_spoofing", "port_scanning", "scan_os",
      "scan_host_discovery"];
  }

  chartOptionColors_light: Map<string, string> = new Map()
    .set('angleLinesColor', "#545454")
    .set('gridLinesColor', "#000000")
    .set('pointLabelsColor', "#000000")
    .set('legendColor', "#545454")

  chartOptionColors_dark: Map<string, string> = new Map()
    .set('angleLinesColor', "#fdfbff")
    .set('gridLinesColor', "#fdfbff")
    .set('pointLabelsColor', "#fdfbff")
    .set('legendColor', "#fdfbff")

  chartOptionsColorMap: Map<string, Map<string, string>> = new Map()
    .set("light", this.chartOptionColors_light)
    .set("dark", this.chartOptionColors_dark)

  public updateChartOptions(theme: Theme) {
    this.chartOptions = {
      scale: {
        angleLines: {
          color: this.chartOptionsColorMap.get(theme.name)?.get("angleLinesColor"),
        },

        gridLines: {
          color: this.chartOptionsColorMap.get(theme.name)?.get("gridLinesColor")
        },
        pointLabels: {
          fontColor: this.chartOptionsColorMap.get(theme.name)?.get("pointLabelsColor")
        },
        ticks: {
          backdropColor: "#00ffff",
          showLabelBackdrop: false,
          display: false
        },
      },
      responsive: true,
      legend: {
        position: 'top',
        labels: {
          usePointStyle: true,
          fontColor: this.chartOptionsColorMap.get(theme.name)?.get("legendColor")
        },


      }
    };
  }



}
