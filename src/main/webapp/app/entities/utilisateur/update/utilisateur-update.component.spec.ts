import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ICV } from 'app/entities/cv/cv.model';
import { CVService } from 'app/entities/cv/service/cv.service';
import { IUtilisateur } from '../utilisateur.model';
import { UtilisateurService } from '../service/utilisateur.service';
import { UtilisateurFormService } from './utilisateur-form.service';

import { UtilisateurUpdateComponent } from './utilisateur-update.component';

describe('Utilisateur Management Update Component', () => {
  let comp: UtilisateurUpdateComponent;
  let fixture: ComponentFixture<UtilisateurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let utilisateurFormService: UtilisateurFormService;
  let utilisateurService: UtilisateurService;
  let userService: UserService;
  let cVService: CVService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UtilisateurUpdateComponent],
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
      .overrideTemplate(UtilisateurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UtilisateurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    utilisateurFormService = TestBed.inject(UtilisateurFormService);
    utilisateurService = TestBed.inject(UtilisateurService);
    userService = TestBed.inject(UserService);
    cVService = TestBed.inject(CVService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const utilisateur: IUtilisateur = { id: 31928 };
      const user: IUser = { id: 3944 };
      utilisateur.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ utilisateur });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call cv query and add missing value', () => {
      const utilisateur: IUtilisateur = { id: 31928 };
      const cv: ICV = { id: 12998 };
      utilisateur.cv = cv;

      const cvCollection: ICV[] = [{ id: 12998 }];
      jest.spyOn(cVService, 'query').mockReturnValue(of(new HttpResponse({ body: cvCollection })));
      const expectedCollection: ICV[] = [cv, ...cvCollection];
      jest.spyOn(cVService, 'addCVToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ utilisateur });
      comp.ngOnInit();

      expect(cVService.query).toHaveBeenCalled();
      expect(cVService.addCVToCollectionIfMissing).toHaveBeenCalledWith(cvCollection, cv);
      expect(comp.cvsCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const utilisateur: IUtilisateur = { id: 31928 };
      const user: IUser = { id: 3944 };
      utilisateur.user = user;
      const cv: ICV = { id: 12998 };
      utilisateur.cv = cv;

      activatedRoute.data = of({ utilisateur });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.cvsCollection).toContainEqual(cv);
      expect(comp.utilisateur).toEqual(utilisateur);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUtilisateur>>();
      const utilisateur = { id: 2179 };
      jest.spyOn(utilisateurFormService, 'getUtilisateur').mockReturnValue(utilisateur);
      jest.spyOn(utilisateurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ utilisateur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: utilisateur }));
      saveSubject.complete();

      // THEN
      expect(utilisateurFormService.getUtilisateur).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(utilisateurService.update).toHaveBeenCalledWith(expect.objectContaining(utilisateur));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUtilisateur>>();
      const utilisateur = { id: 2179 };
      jest.spyOn(utilisateurFormService, 'getUtilisateur').mockReturnValue({ id: null });
      jest.spyOn(utilisateurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ utilisateur: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: utilisateur }));
      saveSubject.complete();

      // THEN
      expect(utilisateurFormService.getUtilisateur).toHaveBeenCalled();
      expect(utilisateurService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUtilisateur>>();
      const utilisateur = { id: 2179 };
      jest.spyOn(utilisateurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ utilisateur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(utilisateurService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCV', () => {
      it('should forward to cVService', () => {
        const entity = { id: 12998 };
        const entity2 = { id: 6278 };
        jest.spyOn(cVService, 'compareCV');
        comp.compareCV(entity, entity2);
        expect(cVService.compareCV).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
