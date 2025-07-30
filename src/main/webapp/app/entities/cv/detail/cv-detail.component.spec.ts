import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CVDetailComponent } from './cv-detail.component';

describe('CV Management Detail Component', () => {
  let comp: CVDetailComponent;
  let fixture: ComponentFixture<CVDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CVDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./cv-detail.component').then(m => m.CVDetailComponent),
              resolve: { cV: () => of({ id: 12998 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CVDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CVDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load cV on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CVDetailComponent);

      // THEN
      expect(instance.cV()).toEqual(expect.objectContaining({ id: 12998 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
