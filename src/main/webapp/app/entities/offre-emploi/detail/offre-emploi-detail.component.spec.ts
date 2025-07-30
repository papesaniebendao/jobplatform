import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { OffreEmploiDetailComponent } from './offre-emploi-detail.component';

describe('OffreEmploi Management Detail Component', () => {
  let comp: OffreEmploiDetailComponent;
  let fixture: ComponentFixture<OffreEmploiDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OffreEmploiDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./offre-emploi-detail.component').then(m => m.OffreEmploiDetailComponent),
              resolve: { offreEmploi: () => of({ id: 12430 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(OffreEmploiDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OffreEmploiDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load offreEmploi on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', OffreEmploiDetailComponent);

      // THEN
      expect(instance.offreEmploi()).toEqual(expect.objectContaining({ id: 12430 }));
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
