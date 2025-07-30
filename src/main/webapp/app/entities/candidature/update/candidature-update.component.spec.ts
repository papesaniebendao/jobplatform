import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';
import { UtilisateurService } from 'app/entities/utilisateur/service/utilisateur.service';
import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { ICandidature } from '../candidature.model';
import { CandidatureService } from '../service/candidature.service';
import { CandidatureFormService } from './candidature-form.service';

import { CandidatureUpdateComponent } from './candidature-update.component';

describe('Candidature Management Update Component', () => {
  let comp: CandidatureUpdateComponent;
  let fixture: ComponentFixture<CandidatureUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let candidatureFormService: CandidatureFormService;
  let candidatureService: CandidatureService;
  let utilisateurService: UtilisateurService;
  let offreEmploiService: OffreEmploiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CandidatureUpdateComponent],
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
      .overrideTemplate(CandidatureUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CandidatureUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    candidatureFormService = TestBed.inject(CandidatureFormService);
    candidatureService = TestBed.inject(CandidatureService);
    utilisateurService = TestBed.inject(UtilisateurService);
    offreEmploiService = TestBed.inject(OffreEmploiService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Utilisateur query and add missing value', () => {
      const candidature: ICandidature = { id: 12242 };
      const candidat: IUtilisateur = { id: 2179 };
      candidature.candidat = candidat;

      const utilisateurCollection: IUtilisateur[] = [{ id: 2179 }];
      jest.spyOn(utilisateurService, 'query').mockReturnValue(of(new HttpResponse({ body: utilisateurCollection })));
      const additionalUtilisateurs = [candidat];
      const expectedCollection: IUtilisateur[] = [...additionalUtilisateurs, ...utilisateurCollection];
      jest.spyOn(utilisateurService, 'addUtilisateurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      expect(utilisateurService.query).toHaveBeenCalled();
      expect(utilisateurService.addUtilisateurToCollectionIfMissing).toHaveBeenCalledWith(
        utilisateurCollection,
        ...additionalUtilisateurs.map(expect.objectContaining),
      );
      expect(comp.utilisateursSharedCollection).toEqual(expectedCollection);
    });

    it('should call OffreEmploi query and add missing value', () => {
      const candidature: ICandidature = { id: 12242 };
      const offreEmploi: IOffreEmploi = { id: 12430 };
      candidature.offreEmploi = offreEmploi;

      const offreEmploiCollection: IOffreEmploi[] = [{ id: 12430 }];
      jest.spyOn(offreEmploiService, 'query').mockReturnValue(of(new HttpResponse({ body: offreEmploiCollection })));
      const additionalOffreEmplois = [offreEmploi];
      const expectedCollection: IOffreEmploi[] = [...additionalOffreEmplois, ...offreEmploiCollection];
      jest.spyOn(offreEmploiService, 'addOffreEmploiToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      expect(offreEmploiService.query).toHaveBeenCalled();
      expect(offreEmploiService.addOffreEmploiToCollectionIfMissing).toHaveBeenCalledWith(
        offreEmploiCollection,
        ...additionalOffreEmplois.map(expect.objectContaining),
      );
      expect(comp.offreEmploisSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const candidature: ICandidature = { id: 12242 };
      const candidat: IUtilisateur = { id: 2179 };
      candidature.candidat = candidat;
      const offreEmploi: IOffreEmploi = { id: 12430 };
      candidature.offreEmploi = offreEmploi;

      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      expect(comp.utilisateursSharedCollection).toContainEqual(candidat);
      expect(comp.offreEmploisSharedCollection).toContainEqual(offreEmploi);
      expect(comp.candidature).toEqual(candidature);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICandidature>>();
      const candidature = { id: 17844 };
      jest.spyOn(candidatureFormService, 'getCandidature').mockReturnValue(candidature);
      jest.spyOn(candidatureService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: candidature }));
      saveSubject.complete();

      // THEN
      expect(candidatureFormService.getCandidature).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(candidatureService.update).toHaveBeenCalledWith(expect.objectContaining(candidature));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICandidature>>();
      const candidature = { id: 17844 };
      jest.spyOn(candidatureFormService, 'getCandidature').mockReturnValue({ id: null });
      jest.spyOn(candidatureService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ candidature: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: candidature }));
      saveSubject.complete();

      // THEN
      expect(candidatureFormService.getCandidature).toHaveBeenCalled();
      expect(candidatureService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICandidature>>();
      const candidature = { id: 17844 };
      jest.spyOn(candidatureService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(candidatureService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUtilisateur', () => {
      it('should forward to utilisateurService', () => {
        const entity = { id: 2179 };
        const entity2 = { id: 31928 };
        jest.spyOn(utilisateurService, 'compareUtilisateur');
        comp.compareUtilisateur(entity, entity2);
        expect(utilisateurService.compareUtilisateur).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareOffreEmploi', () => {
      it('should forward to offreEmploiService', () => {
        const entity = { id: 12430 };
        const entity2 = { id: 17338 };
        jest.spyOn(offreEmploiService, 'compareOffreEmploi');
        comp.compareOffreEmploi(entity, entity2);
        expect(offreEmploiService.compareOffreEmploi).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
