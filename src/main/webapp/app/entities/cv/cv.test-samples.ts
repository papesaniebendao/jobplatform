import { ICV, NewCV } from './cv.model';

export const sampleWithRequiredData: ICV = {
  id: 14385,
  urlFichier: 'étant donné que',
};

export const sampleWithPartialData: ICV = {
  id: 14379,
  urlFichier: 'mettre en outre de',
};

export const sampleWithFullData: ICV = {
  id: 18597,
  urlFichier: 'sitôt que',
};

export const sampleWithNewData: NewCV = {
  urlFichier: 'candide',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
