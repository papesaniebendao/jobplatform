import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { TypeContratService } from '../service/type-contrat.service';
import { ITypeContrat } from '../type-contrat.model';
import { TypeContratFormService } from './type-contrat-form.service';

import { TypeContratUpdateComponent } from './type-contrat-update.component';

describe('TypeContrat Management Update Component', () => {
  let comp: TypeContratUpdateComponent;
  let fixture: ComponentFixture<TypeContratUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let typeContratFormService: TypeContratFormService;
  let typeContratService: TypeContratService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TypeContratUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TypeContratUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TypeContratUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    typeContratFormService = TestBed.inject(TypeContratFormService);
    typeContratService = TestBed.inject(TypeContratService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const typeContrat: ITypeContrat = { id: 16761 };

      activatedRoute.data = of({ typeContrat });
      comp.ngOnInit();

      expect(comp.typeContrat).toEqual(typeContrat);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITypeContrat>>();
      const typeContrat = { id: 13691 };
      jest.spyOn(typeContratFormService, 'getTypeContrat').mockReturnValue(typeContrat);
      jest.spyOn(typeContratService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ typeContrat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: typeContrat }));
      saveSubject.complete();

      // THEN
      expect(typeContratFormService.getTypeContrat).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(typeContratService.update).toHaveBeenCalledWith(expect.objectContaining(typeContrat));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITypeContrat>>();
      const typeContrat = { id: 13691 };
      jest.spyOn(typeContratFormService, 'getTypeContrat').mockReturnValue({ id: null });
      jest.spyOn(typeContratService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ typeContrat: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: typeContrat }));
      saveSubject.complete();

      // THEN
      expect(typeContratFormService.getTypeContrat).toHaveBeenCalled();
      expect(typeContratService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITypeContrat>>();
      const typeContrat = { id: 13691 };
      jest.spyOn(typeContratService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ typeContrat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(typeContratService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
