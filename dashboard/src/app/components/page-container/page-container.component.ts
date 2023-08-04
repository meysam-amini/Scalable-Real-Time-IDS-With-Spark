import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, NgZone, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LineChartComponent} from "../charts/line-chart/line-chart.component";
import {ChartService} from "../../services/chart.service";
import {NgbModal, ModalDismissReasons, NgbModule, NgbDate} from '@ng-bootstrap/ng-bootstrap';
import {map, mergeAll, tap} from "rxjs/operators";
import {Subject} from "rxjs/internal/Subject";
import {HttpClient} from "@angular/common/http";
import {ApiService} from "../../services/api.service";
import {MonitoringZone} from "../../model/monitoring-zone";
import {ThemeService} from "../../theme/theme.service";


@Component({
  selector: 'app-page-container',
  templateUrl: './page-container.component.html',
  styleUrls: ['./page-container.component.css']
})
export class PageContainerComponent implements OnInit {

  pages: Array<string> = ["line_charts", "radar_chart", "data_table"];


 // zones: Array<MonitoringZone> =[];// [];//['No Source'];
  whichPage: string = 'line_charts';
  //charts: LineChartComponent[] = [];
  subscriptionCount = 3;

  closeResult = '';

  public ZONES: Array<MonitoringZone> =new Array<MonitoringZone>();


  ///for data gathering:
  public ALL_EVENT_SOURCES: Array<EventSource> = new Array<EventSource>();

  private numberOfModems: number = 0;

  initFlag:boolean=true;

  constructor(private route: ActivatedRoute,
              private chartService: ChartService,
              private modalService: NgbModal,
              private httpClient: HttpClient,
              private ngZone: NgZone,
              private apiService:ApiService,
              private themeService:ThemeService) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.whichPage = this.route.snapshot.paramMap.get('keyword') as string;

      if (this.pages.indexOf(this.whichPage) == -1)
        this.whichPage = '';
      console.log(this.whichPage);
    });


    this.listenOnSetAllApis();
  }

  // lineChartCount:number=2;
  // addLineChart() {
  //   this.lineChartCount++;
  //   this.allLineChartsData.push(this.lineChartCount);
  // }

  // radarChartsDataTemp:Array<string>=["modem1", "modem2", "modem3", "modem4",
  // "modem5","modem6","modem7"];
  // radarChartCount:number=3;



  public listenOnSetAllApis() {

    this.apiService.setAllApisListener.subscribe((apis:Array<MonitoringZone>)=>{
      this.ZONES=apis;
    //   this.setZones(apis);
    //   console.log('zones: '+this.ZONES)
      this.getAllZonesData();
    });


  }



  private getAllZonesData() {
   for (let i=0;i<this.ZONES.length;i++){
     this.ALL_EVENT_SOURCES[i] = new EventSource(this.ZONES[i].url);
     this.chartService.ALL_APIs.push(new Subject<any>());
     this.ALL_EVENT_SOURCES[i].onmessage = msg => {
       this.ngZone.runOutsideAngular(() => {
         let obj:any={}
         obj['zone']=this.ZONES[i].name;
         // console.log(msg.data)
         obj['msg']=msg.data;
         this.chartService.ALL_APIs[i].next(obj);

       });

     };
   }

    this.chartService.startChartMonitoring(this.ZONES);
  }


  /*private getAllZonesData() {
    let url0 = "http://localhost:8080/allApis";

    return this.httpClient.get(url0).pipe(
      // tap((r: any) => r),
      // mergeAll(),
      tap((res: any) => {

        for(let i=0;i<res.length;i++){
          // console.log('i:'+res[i].url)
          this.ALL_EVENT_SOURCES[i] = new EventSource(res[i].url);
          this.chartService.ALL_APIs.push(new Subject<any>());
          this.ALL_EVENT_SOURCES[i].onmessage = msg => {
           this.ngZone.runOutsideAngular(() => {
              let obj:any={}
              obj['zone']=res[i].name;
              obj['msg']=msg.data
              this.chartService.ALL_APIs[i].next(obj);

           });

          };
        }


          // this.chartService.ALL_APIs[this.numberOfModems].subscribe((data:any)=>{
          //   // data=JSON.parse(data.data);
          //   console.log('data from ',data.zone,': ',JSON.parse(data.msg).prediction)
          // })

          if (this.numberOfModems < 2)
            this.numberOfModems++;
        }
      )
      ,
    )
      .subscribe(d => {
        if (this.chartService.ALL_APIs.length == this.zones.length ) {
          this.chartService.startChartMonitoring(this.zones);
          // this.initFlag=false;
        }
      });

  }*/

  toggleTheme() {
    if (this.themeService.isDarkTheme()) {
      this.themeService.setLightTheme();
    } else {
      this.themeService.setDarkTheme();
    }
  }



}
