import { Injectable } from '@angular/core';
import {dark, light, Theme} from "./theme";
import {Subject} from "rxjs/internal/Subject";


@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private active: Theme = dark;
  private availableThemes: Theme[] = [light, dark];

  currentTheme: Subject<Theme> =new Subject<Theme>();

  getAvailableThemes(): Theme[] {
    return this.availableThemes;
  }

  getActiveTheme(): Theme {
    return this.active;
  }

  isDarkTheme(): boolean {
    return this.active.name === dark.name;
  }

  setDarkTheme(): void {
    this.setActiveTheme(dark);
  }

  setLightTheme(): void {
    this.setActiveTheme(light);
  }

  setActiveTheme(theme: Theme): void {
    this.active = theme;
    //publish theme for charts updating
    this.currentTheme.next(theme);

    Object.keys(this.active.properties).forEach(property => {
      document.documentElement.style.setProperty(
        property,
        this.active.properties[property]
      );
    });
  }
}
