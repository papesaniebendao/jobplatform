import dayjs from 'dayjs/esm';
import { IUtilisateur } from 'app/entities/utilisateur/utilisateur.model';
import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { StatutCandidature } from 'app/entities/enumerations/statut-candidature.model';

export interface ICandidature {
  id: number;
  datePostulation?: dayjs.Dayjs | null;
  statut?: keyof typeof StatutCandidature | null;
  candidat?: Pick<IUtilisateur, 'id'> | null;
  offreEmploi?: Pick<IOffreEmploi, 'id'> | null;
}

export type NewCandidature = Omit<ICandidature, 'id'> & { id: null };
