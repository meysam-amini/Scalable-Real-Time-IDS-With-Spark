import {Injectable, NgZone} from '@angular/core';
import {Subject} from "rxjs/internal/Subject";
import {Observable} from "rxjs/internal/Observable";
import {MonitoringZone} from "../model/monitoring-zone";


@Injectable({
  providedIn: 'root'
})
export class ChartService {

  // linearChartsData: Subject<number> =new Subject<number>();
  // radarChartData: Subject<number> =new Subject<number>();

  public ALL_APIs: Array<Subject<any>> = new Array();


  ChartMonitoring: Subject<Array<MonitoringZone>> =new Subject<Array<MonitoringZone>>();
  myObservables: Array<Subject<any>> = new Array();

  constructor(private _ngzone: NgZone) {
    this.myObservables[0]=new Subject<string>();

  }

  getSSE(url: string): Observable<any> {
    return new Observable<any>(observer => {
      const eventSource =  new EventSource(url);

      eventSource.onmessage = event => {
        this._ngzone.run(() => {
         // let data = JSON.parse(event.data);
         observer.next(event);
        });
       // return ()=> eventSource.close();
      }

      /*eventSource.onerror = error => {
        this._ngzone.run(() => {
          observer.next(error);
        });
      }*/
    });
  }

  private getEventSource(url: string): EventSource {
    return new EventSource(url);
  }

  public startChartMonitoring(zones:Array<MonitoringZone>){
   this.ChartMonitoring.next(zones);
  }
}
