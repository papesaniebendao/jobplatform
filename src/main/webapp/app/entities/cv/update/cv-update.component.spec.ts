import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { CVService } from '../service/cv.service';
import { ICV } from '../cv.model';
import { CVFormService } from './cv-form.service';

import { CVUpdateComponent } from './cv-update.component';

describe('CV Management Update Component', () => {
  let comp: CVUpdateComponent;
  let fixture: ComponentFixture<CVUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cVFormService: CVFormService;
  let cVService: CVService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CVUpdateComponent],
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
      .overrideTemplate(CVUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CVUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cVFormService = TestBed.inject(CVFormService);
    cVService = TestBed.inject(CVService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const cV: ICV = { id: 6278 };

      activatedRoute.data = of({ cV });
      comp.ngOnInit();

      expect(comp.cV).toEqual(cV);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICV>>();
      const cV = { id: 12998 };
      jest.spyOn(cVFormService, 'getCV').mockReturnValue(cV);
      jest.spyOn(cVService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cV });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cV }));
      saveSubject.complete();

      // THEN
      expect(cVFormService.getCV).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cVService.update).toHaveBeenCalledWith(expect.objectContaining(cV));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICV>>();
      const cV = { id: 12998 };
      jest.spyOn(cVFormService, 'getCV').mockReturnValue({ id: null });
      jest.spyOn(cVService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cV: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cV }));
      saveSubject.complete();

      // THEN
      expect(cVFormService.getCV).toHaveBeenCalled();
      expect(cVService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICV>>();
      const cV = { id: 12998 };
      jest.spyOn(cVService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cV });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cVService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
