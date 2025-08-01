import { IUtilisateur, NewUtilisateur } from './utilisateur.model';

export const sampleWithRequiredData: IUtilisateur = {
  id: 7635,
  role: 'CANDIDAT',
  isActive: false,
};

export const sampleWithPartialData: IUtilisateur = {
  id: 23519,
  telephone: '0637660345',
  role: 'RECRUTEUR',
  isActive: false,
};

export const sampleWithFullData: IUtilisateur = {
  id: 18250,
  prenom: 'si bien que concernant',
  nom: 'résister',
  nomEntreprise: 'parce que groin groin autant',
  secteurActivite: 'préciser tirer personnel',
  telephone: '0648025218',
  role: 'RECRUTEUR',
  isActive: true,
};

export const sampleWithNewData: NewUtilisateur = {
  role: 'CANDIDAT',
  isActive: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
