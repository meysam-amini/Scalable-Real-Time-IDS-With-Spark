import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import Api = DataTables.Api;
import {ApiService} from "../../services/api.service";
import {Subject} from "rxjs/internal/Subject";
import {ChartService} from "../../services/chart.service";
import {ThemeService} from "../../theme/theme.service";
import {ChartDataSets, ChartOptions, ChartType} from "chart.js";
import {Theme} from "../../theme/theme";
import {Color, Label} from "ng2-charts";
import {MatTableDataSource} from "@angular/material/table";
import {myDataSource} from "./myDataSource";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {Netflow} from "../../model/netflow";

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.css']
})
export class DataTableComponent implements OnInit,AfterViewInit {

  @Input() subscriptionIndex: any;
 // @Input() zones: Array<string>= [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

 displayedColumns: string[] = ['SrcAddr', 'DstAddr', 'SrcMac', 'DstMac','prediction','probability'];


  attack_types = ["normal", "syn_flooding", "ack_flooding",
    "host_bruteforce", "http_flooding", "udp_flooding",
    "arp_spoofing", "port_scanning", "scan_os",
    "scan_host_discovery"];


  data:Array<Netflow>=[];
  dataSource :MatTableDataSource<Netflow>= new MatTableDataSource(this.data);
  // dataSource=new myDataSource(this.data);


  private dataPipeReady: boolean = false;


  constructor(public chartService: ChartService,
              elmref:ElementRef,
              themeService: ThemeService) {
  }


  ngOnInit(): void {
    this.chartService.ChartMonitoring.subscribe(zones => {
      // here we don't need the zones array, and we just want
      // to be aware of setting new apis
    //  console.log('zones',zones);
      this.startMonitor();
    })

  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }



  private startMonitor() {
   // console.log('subscriptionIndex: ',this.subscriptionIndex)

    this.chartService.ALL_APIs[this.subscriptionIndex].subscribe(data => {
     // console.log("data from DT: "+ JSON.parse(data.msg))
      this.pushData(JSON.parse(data.msg));
    })


  }


  private pushData(row:any) {
    this.data.push(this.mapData(row));
    this.dataSource.data=(this.data);
  }

  private mapData(data:any):Netflow{
    let tmp:Netflow=new Netflow();
    tmp.SrcAddr=data.srcAddr;
    tmp.DstAddr=data.dstAddr;
    tmp.SrcMac=data.srcMac;
    tmp.DstMac=data.dstMac;
    tmp.prediction=this.attack_types[data.prediction];
    tmp.probability=data.probability;
    return tmp;
  }


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

}
