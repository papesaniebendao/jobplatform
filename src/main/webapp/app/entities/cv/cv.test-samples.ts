import dayjs from 'dayjs/esm';

import { ICV, NewCV } from './cv.model';

export const sampleWithRequiredData: ICV = {
  id: 14385,
  urlFichier: 'étant donné que',
};

export const sampleWithPartialData: ICV = {
  id: 13276,
  urlFichier: 'par jeter avant de',
  nomFichier: 'au point que instruire secours',
};

export const sampleWithFullData: ICV = {
  id: 18597,
  urlFichier: 'sitôt que',
  nomFichier: 'jeune enfant dense',
  dateUpload: dayjs('2025-07-27T01:30'),
};

export const sampleWithNewData: NewCV = {
  urlFichier: 'candide',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
