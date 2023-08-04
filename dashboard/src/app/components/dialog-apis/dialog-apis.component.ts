import { Component, OnInit } from '@angular/core';
import {PageContainerComponent} from "../page-container/page-container.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import allApisJson from '../../../assets/allApis.json';
import {MonitoringZone} from "../../model/monitoring-zone";
import {FormControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatButton} from "@angular/material/button";
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {ApiService} from "../../services/api.service";

@Component({
  providers:[PageContainerComponent],
  selector: 'app-dialog-apis',
  templateUrl: './dialog-apis.component.html',
  styleUrls: ['./dialog-apis.component.css']
})
export class DialogApisComponent implements OnInit {

  allApis:MonitoringZone[]=allApisJson;
  nameControl = new FormControl('');

  urlPattern =
    Validators.pattern('^(http|https?://)([0-9]{1,3}(?:.[0-9]{1,3}){3}|[a-zA-Z]+):([0-9]{1,5})(/[a-z0-9_-]+)$');
  namePattern = "^[a-z0-9_-]{0,15}$";
  // isValidFormSubmitted :any= false;

  APIsForm:FormGroup = this.formBuilder.group({
    url: ['', [Validators.required, this.urlPattern]],
    name: ['', [Validators.required, Validators.pattern(this.namePattern)]]
  });

  constructor(private apiService:ApiService,
              private modalService:NgbModal,
              private formBuilder:FormBuilder) { }

  ngOnInit(): void {
  }


  openDialog(content:any) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title',windowClass: 'my-class'}).result.then((result) => {
      // this.closeResult =''; //Closed with: ${result}`;
      this.apiService.setAllApis(this.allApis);
    }, (reason) => {
      this.APIsForm.reset();
      //  this.closeResult = '';//Dismissed ${this.getDismissReason(reason)}`;
    });
  }


  addNewModemDataApi(modemApi:MonitoringZone) {
    this.allApis.push(modemApi);
    console.log(this.allApis);

  }

  removeApi(index:number){
    console.log(index);
    this.allApis.splice(index,1);
  }

  onFormSubmit() {
    // this.isValidFormSubmitted = false;
    if (this.APIsForm.invalid) {
      return;
    }
    // this.isValidFormSubmitted = false;
    let modemDataApi: MonitoringZone = this.APIsForm.value;
    this.addNewModemDataApi(modemDataApi);
    this.APIsForm.reset();
  }

  get name() {
    return this.APIsForm.get('name');
  }

  get url() {
    return this.APIsForm.get('url');
  }
}
