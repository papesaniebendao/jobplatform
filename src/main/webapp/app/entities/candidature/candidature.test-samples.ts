import dayjs from 'dayjs/esm';

import { ICandidature, NewCandidature } from './candidature.model';

export const sampleWithRequiredData: ICandidature = {
  id: 28558,
  datePostulation: dayjs('2025-07-27T08:25'),
  statut: 'EN_ATTENTE',
};

export const sampleWithPartialData: ICandidature = {
  id: 14253,
  datePostulation: dayjs('2025-07-27T18:56'),
  statut: 'ACCEPTEE',
};

export const sampleWithFullData: ICandidature = {
  id: 23153,
  datePostulation: dayjs('2025-07-27T22:16'),
  statut: 'REFUSEE',
};

export const sampleWithNewData: NewCandidature = {
  datePostulation: dayjs('2025-07-27T02:22'),
  statut: 'REFUSEE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
