import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TypeContratDetailComponent } from './type-contrat-detail.component';

describe('TypeContrat Management Detail Component', () => {
  let comp: TypeContratDetailComponent;
  let fixture: ComponentFixture<TypeContratDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TypeContratDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./type-contrat-detail.component').then(m => m.TypeContratDetailComponent),
              resolve: { typeContrat: () => of({ id: 13691 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TypeContratDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TypeContratDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load typeContrat on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TypeContratDetailComponent);

      // THEN
      expect(instance.typeContrat()).toEqual(expect.objectContaining({ id: 13691 }));
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
