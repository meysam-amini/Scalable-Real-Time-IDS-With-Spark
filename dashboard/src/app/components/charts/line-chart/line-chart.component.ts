import {AfterViewInit, Component, ElementRef, Input, NgZone, OnInit, ViewChild} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType} from "chart.js";
import {ChartService} from "../../../services/chart.service";
import {Label} from "ng2-charts";
import {BaseChartComponent} from "../base-chart/base-chart.component";
import {ThemeService} from "../../../theme/theme.service";
import {Theme} from "../../../theme/theme";

@Component({
  selector: 'app-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrls: ['./line-chart.component.css']
})
export class LineChartComponent extends BaseChartComponent
  implements OnInit,AfterViewInit {

  @Input() subscriptionIndex: any;
  // @Input() zones: Array<string>=[];

  //chart properties
  chartType: ChartType = 'line';
  groupColors: Array<any> = [
    '#13d315',
    '#fd0401'
  ];

  num_zones: number = 2;
  private XAxis: number = 20;
  private sumOfEachGroupPerMinute:Array<number>=[0,0];
  private dataPipeReady: boolean = false;




  dataLables = ["normal", "anomalies"];

  constructor(public chartService: ChartService,
              elmref:ElementRef,
              themeService: ThemeService) {

    super(chartService,elmref,themeService);



  }


  ngOnInit(): void {
    super.ngOnInit();
    this.setUpdateInterval(800);
    this.chartService.ChartMonitoring.subscribe(zones => {
      // here we don't need the zones array, and we just want
      // to be aware of setting new apis
      this.startMonitor();
    })

  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
  }



  private startMonitor() {

    // this.setDataLables();
    this.initChart();
    this.ngAfterViewInit();
//    console.log('subscriptionIndex: ',this.subscriptionIndex)

      this.chartService.ALL_APIs[this.subscriptionIndex].subscribe(data => {
        // console.log('data from ',,': ',JSON.parse(data.msg).prediction)
        this.dataPipeReady=true;

        this.pushData(0, JSON.parse(data.msg).prediction);
      })


  }


  public setUpdateInterval(interval:number){
    super.setUpdateInterval(interval);
  }

  public pushData(zone: number,attackType:number) {

   // console.log(this.subscriptionIndex+" : "+attackType);
    if(attackType!=0)
      attackType=1;

    //increment each attackType count in current time window(minute_index)
    this.sumOfEachGroupPerMinute[attackType] ++;
  }

  public updateChart(): void {

    if(this.dataPipeReady) {

      for (let groupIndex = 0; groupIndex < this.dataLables.length; groupIndex++) {
        // console.log('updating... : ', this.sumOfEachGroupPerMinute[zoneIndex]);

        this.chartDataSets[groupIndex].data?.unshift(this.sumOfEachGroupPerMinute[groupIndex]);

        this.sumOfEachGroupPerMinute[groupIndex] = 0;

      }
      //update chart
      this.chartDataSets.slice();

      //end of time window => begin from 0
      // if (this.minute_index >= this.XAxis) {
      //   this.minute_index = 0;
      // }}
    }
  }


  public createLabels(){
    this.chartLabels = [];
    for (let index = 0; index < this.XAxis; index++) {
      this.chartLabels.push(index.toString());
    }
  }

  // public setDataLables(){
  //   super.setDataLables(this.dataLables);
  // }




  chartOptionColors_light: Map<string, string> = new Map()
    .set('xAxes_fontColor', "#545454")
    .set('xAxes_gridLinesColor', "#ffffff")
    .set('yAxes_fontColor', "#545454")
    .set('yAxes_gridLinesColor', "#000000")
    .set('legendColor', "#545454");

  chartOptionColors_dark: Map<string, string> = new Map()
    .set('xAxes_fontColor', "#fdfbff")
    .set('xAxes_gridLinesColor', "#545454")
    .set('yAxes_fontColor', "#fdfbff")
    .set('yAxes_gridLinesColor', "#fdfbff")
    .set('legendColor', "#fdfbff");

  chartOptionsColorMap: Map<string, Map<string, string>> = new Map()
    .set("light", this.chartOptionColors_light)
    .set("dark", this.chartOptionColors_dark);

  public updateChartOptions(theme:Theme){

    this.chartOptions = {

      responsive: true,
      scales: {
        xAxes: [{
          ticks: {
            fontSize: 8,
            fontColor:this.chartOptionsColorMap.
            get(theme.name)?.get("xAxes_fontColor")
          },
          gridLines: {
            color: this.chartOptionsColorMap.
            get(theme.name)?.get("xAxes_gridLinesColor"),
            lineWidth: 0.1
          }
        }],
        yAxes: [{
          ticks: {
            fontSize: 7,
            fontColor: this.chartOptionsColorMap.
            get(theme.name)?.get("yAxes_fontColor")
          },
          gridLines: {
            color: this.chartOptionsColorMap.
            get(theme.name)?.get("yAxes_gridLinesColor"),
            lineWidth: 0.2
          }
        }]
      },
      legend: {
        position: 'top',
        labels: {
          usePointStyle: true,
          fontColor: this.chartOptionsColorMap.
          get(theme.name)?.get("legendColor")

        },


      }
    };
  }

}
