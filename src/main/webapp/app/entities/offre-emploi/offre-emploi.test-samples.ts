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
  id: 23706,
  titre: 'commis de cuisine délégation auprès de',
  description: 'à partir de',
  localisation: 'si bien que',
  salaire: 11209.14,
  datePublication: dayjs('2025-07-27T01:54'),
  dateExpiration: dayjs('2025-07-27T05:28'),
};

export const sampleWithFullData: IOffreEmploi = {
  id: 19852,
  titre: 'fumer ha ha',
  description: 'incalculable puisque',
  localisation: 'durant cot cot',
  salaire: 24227.38,
  datePublication: dayjs('2025-07-27T09:54'),
  dateExpiration: dayjs('2025-07-27T14:52'),
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
