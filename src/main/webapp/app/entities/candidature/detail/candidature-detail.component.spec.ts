import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CandidatureDetailComponent } from './candidature-detail.component';

describe('Candidature Management Detail Component', () => {
  let comp: CandidatureDetailComponent;
  let fixture: ComponentFixture<CandidatureDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CandidatureDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./candidature-detail.component').then(m => m.CandidatureDetailComponent),
              resolve: { candidature: () => of({ id: 17844 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CandidatureDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidatureDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load candidature on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CandidatureDetailComponent);

      // THEN
      expect(instance.candidature()).toEqual(expect.objectContaining({ id: 17844 }));
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
