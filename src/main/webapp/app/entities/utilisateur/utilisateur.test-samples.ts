import { IUtilisateur, NewUtilisateur } from './utilisateur.model';

export const sampleWithRequiredData: IUtilisateur = {
  id: 7635,
  nom: 'vaste derechef',
  role: 'CANDIDAT',
  isActive: false,
};

export const sampleWithPartialData: IUtilisateur = {
  id: 19905,
  nom: 'moderne au prix de exclure',
  role: 'RECRUTEUR',
  isActive: true,
};

export const sampleWithFullData: IUtilisateur = {
  id: 18250,
  nom: 'si bien que concernant',
  telephone: '0210962487',
  role: 'RECRUTEUR',
  isActive: true,
};

export const sampleWithNewData: NewUtilisateur = {
  nom: 'hebdomadaire',
  role: 'CANDIDAT',
  isActive: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
