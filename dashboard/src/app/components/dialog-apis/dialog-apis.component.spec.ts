import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogApisComponent } from './dialog-apis.component';

describe('DialogApisComponent', () => {
  let component: DialogApisComponent;
  let fixture: ComponentFixture<DialogApisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogApisComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogApisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
