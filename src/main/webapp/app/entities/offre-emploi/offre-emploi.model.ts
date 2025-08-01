import dayjs from 'dayjs/esm';
import { ITypeContrat } from 'app/entities/type-contrat/type-contrat.model';
import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';

export interface IOffreEmploi {
  id: number;
  titre?: string | null;
  description?: string | null;
  localisation?: string | null;
  salaire?: number | null;
  datePublication?: dayjs.Dayjs | null;
  dateExpiration?: dayjs.Dayjs | null;
  isActive?: boolean | null;
  typeContrat?: Pick<ITypeContrat, 'id'> | null;
  recruteur?: Pick<IUtilisateur, 'id'> | null;
}

export type NewOffreEmploi = Omit<IOffreEmploi, 'id'> & { id: null };
