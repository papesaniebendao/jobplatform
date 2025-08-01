import dayjs from 'dayjs/esm';

import { IOffreEmploi, NewOffreEmploi } from './offre-emploi.model';

export const sampleWithRequiredData: IOffreEmploi = {
  id: 11534,
  titre: 'en face de',
  description: 'avant que',
  localisation: 'ha abaisser',
  datePublication: dayjs('2025-07-27T19:53'),
};

export const sampleWithPartialData: IOffreEmploi = {
  id: 28713,
  titre: 'afin de',
  description: 'commis',
  localisation: 'oh',
  salaire: 8725.42,
  datePublication: dayjs('2025-07-27T01:36'),
  dateExpiration: dayjs('2025-07-27T07:15'),
};

export const sampleWithFullData: IOffreEmploi = {
  id: 19852,
  titre: 'fumer ha ha',
  description: 'incalculable puisque',
  localisation: 'durant cot cot',
  salaire: 24227.38,
  datePublication: dayjs('2025-07-27T09:54'),
  dateExpiration: dayjs('2025-07-27T14:52'),
  isActive: false,
};

export const sampleWithNewData: NewOffreEmploi = {
  titre: 'croâ à même parce que',
  description: 'depuis afin que sincère',
  localisation: 'conseil municipal méditer',
  datePublication: dayjs('2025-07-27T10:41'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
