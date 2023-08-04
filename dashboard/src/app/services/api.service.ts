import {Injectable, NgZone} from '@angular/core';
import {Subject} from "rxjs/internal/Subject";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/internal/Observable";
import {Netflow} from "../model/netflow";
import {MonitoringZone} from "../model/monitoring-zone";
import {JsonArray} from "@angular/compiler-cli/ngcc/src/packages/entry_point";
import {map, mergeAll, switchMap, tap, toArray} from "rxjs/operators";
import {combineLatest} from "rxjs/internal/observable/combineLatest";
import {of} from "rxjs/internal/observable/of";
import {Subscription} from "rxjs/internal/Subscription";
import {Subscribable} from "rxjs/internal/types";
import {flatMap} from "rxjs/internal/operators";
import {pipe} from "rxjs/internal-compatibility";
import {delay} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class ApiService {


  public ALL_APIs: Array<Subject<any>> = new Array();
  public ALL_EVENT_SOURCES: Array<EventSource> = new Array<EventSource>();
  public  setAllApisListener:Subject<any>=new Subject<Array<MonitoringZone>>();
  public MODEMS_DATA: Array<any> = [];

  private numberOfModems: number = 0;


  constructor(private httpClient: HttpClient,
              private ngZone: NgZone) {

  }


  public setAllApis(data:Array<MonitoringZone>){
    this.setAllApisListener.next(data);
  }


  /*of: creates an "emit once and complete" stream*/
  private simulateHttp(val: any, d:number) {
    return of(val).pipe(delay(d));
  }
}


