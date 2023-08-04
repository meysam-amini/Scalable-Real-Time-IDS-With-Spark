import { Component, OnInit } from '@angular/core';
import {ThemeService} from "../../theme/theme.service";
import {FormBuilder} from "@angular/forms";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  pages:Array<string>=["line_charts","radar_chart","data_table"];

  constructor(private themeService:ThemeService) { }

  ngOnInit(): void {
  }



}
