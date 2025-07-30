import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITypeContrat } from 'app/entities/type-contrat/type-contrat.model';
import { TypeContratService } from 'app/entities/type-contrat/service/type-contrat.service';
import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';
import { UtilisateurService } from 'app/entities/utilisateur/service/utilisateur.service';
import { IOffreEmploi } from '../offre-emploi.model';
import { OffreEmploiService } from '../service/offre-emploi.service';
import { OffreEmploiFormService } from './offre-emploi-form.service';

import { OffreEmploiUpdateComponent } from './offre-emploi-update.component';

describe('OffreEmploi Management Update Component', () => {
  let comp: OffreEmploiUpdateComponent;
  let fixture: ComponentFixture<OffreEmploiUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let offreEmploiFormService: OffreEmploiFormService;
  let offreEmploiService: OffreEmploiService;
  let typeContratService: TypeContratService;
  let utilisateurService: UtilisateurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OffreEmploiUpdateComponent],
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
      .overrideTemplate(OffreEmploiUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OffreEmploiUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    offreEmploiFormService = TestBed.inject(OffreEmploiFormService);
    offreEmploiService = TestBed.inject(OffreEmploiService);
    typeContratService = TestBed.inject(TypeContratService);
    utilisateurService = TestBed.inject(UtilisateurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call TypeContrat query and add missing value', () => {
      const offreEmploi: IOffreEmploi = { id: 17338 };
      const typeContrat: ITypeContrat = { id: 13691 };
      offreEmploi.typeContrat = typeContrat;

      const typeContratCollection: ITypeContrat[] = [{ id: 13691 }];
      jest.spyOn(typeContratService, 'query').mockReturnValue(of(new HttpResponse({ body: typeContratCollection })));
      const additionalTypeContrats = [typeContrat];
      const expectedCollection: ITypeContrat[] = [...additionalTypeContrats, ...typeContratCollection];
      jest.spyOn(typeContratService, 'addTypeContratToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      expect(typeContratService.query).toHaveBeenCalled();
      expect(typeContratService.addTypeContratToCollectionIfMissing).toHaveBeenCalledWith(
        typeContratCollection,
        ...additionalTypeContrats.map(expect.objectContaining),
      );
      expect(comp.typeContratsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Utilisateur query and add missing value', () => {
      const offreEmploi: IOffreEmploi = { id: 17338 };
      const recruteur: IUtilisateur = { id: 2179 };
      offreEmploi.recruteur = recruteur;

      const utilisateurCollection: IUtilisateur[] = [{ id: 2179 }];
      jest.spyOn(utilisateurService, 'query').mockReturnValue(of(new HttpResponse({ body: utilisateurCollection })));
      const additionalUtilisateurs = [recruteur];
      const expectedCollection: IUtilisateur[] = [...additionalUtilisateurs, ...utilisateurCollection];
      jest.spyOn(utilisateurService, 'addUtilisateurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      expect(utilisateurService.query).toHaveBeenCalled();
      expect(utilisateurService.addUtilisateurToCollectionIfMissing).toHaveBeenCalledWith(
        utilisateurCollection,
        ...additionalUtilisateurs.map(expect.objectContaining),
      );
      expect(comp.utilisateursSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const offreEmploi: IOffreEmploi = { id: 17338 };
      const typeContrat: ITypeContrat = { id: 13691 };
      offreEmploi.typeContrat = typeContrat;
      const recruteur: IUtilisateur = { id: 2179 };
      offreEmploi.recruteur = recruteur;

      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      expect(comp.typeContratsSharedCollection).toContainEqual(typeContrat);
      expect(comp.utilisateursSharedCollection).toContainEqual(recruteur);
      expect(comp.offreEmploi).toEqual(offreEmploi);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOffreEmploi>>();
      const offreEmploi = { id: 12430 };
      jest.spyOn(offreEmploiFormService, 'getOffreEmploi').mockReturnValue(offreEmploi);
      jest.spyOn(offreEmploiService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: offreEmploi }));
      saveSubject.complete();

      // THEN
      expect(offreEmploiFormService.getOffreEmploi).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(offreEmploiService.update).toHaveBeenCalledWith(expect.objectContaining(offreEmploi));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOffreEmploi>>();
      const offreEmploi = { id: 12430 };
      jest.spyOn(offreEmploiFormService, 'getOffreEmploi').mockReturnValue({ id: null });
      jest.spyOn(offreEmploiService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ offreEmploi: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: offreEmploi }));
      saveSubject.complete();

      // THEN
      expect(offreEmploiFormService.getOffreEmploi).toHaveBeenCalled();
      expect(offreEmploiService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOffreEmploi>>();
      const offreEmploi = { id: 12430 };
      jest.spyOn(offreEmploiService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(offreEmploiService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTypeContrat', () => {
      it('should forward to typeContratService', () => {
        const entity = { id: 13691 };
        const entity2 = { id: 16761 };
        jest.spyOn(typeContratService, 'compareTypeContrat');
        comp.compareTypeContrat(entity, entity2);
        expect(typeContratService.compareTypeContrat).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUtilisateur', () => {
      it('should forward to utilisateurService', () => {
        const entity = { id: 2179 };
        const entity2 = { id: 31928 };
        jest.spyOn(utilisateurService, 'compareUtilisateur');
        comp.compareUtilisateur(entity, entity2);
        expect(utilisateurService.compareUtilisateur).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
