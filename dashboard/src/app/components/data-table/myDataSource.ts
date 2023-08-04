import {MonitoringZone} from "../../model/monitoring-zone";
import {DataSource} from "@angular/cdk/table";
import {ReplaySubject} from "rxjs/internal/ReplaySubject";
import {Observable} from "rxjs/internal/Observable";

export class myDataSource extends DataSource<MonitoringZone> {
  private _dataStream = new ReplaySubject<MonitoringZone[]>();

  constructor(initialData: MonitoringZone[]) {
    super();
    this.setData(initialData);
  }

  connect(): Observable<MonitoringZone[]> {
    return this._dataStream;
  }

  disconnect() {}

  setData(data: MonitoringZone[]) {
    this._dataStream.next(data);
  }
}
