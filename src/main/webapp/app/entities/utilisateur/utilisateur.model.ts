import { IUser } from 'app/entities/user/user.model';
import { ICV } from 'app/entities/cv/cv.model';
import { RoleUtilisateur } from 'app/entities/enumerations/role-utilisateur.model';

export interface IUtilisateur {
  id: number;
  prenom?: string | null;
  nom?: string | null;
  nomEntreprise?: string | null;
  secteurActivite?: string | null;
  telephone?: string | null;
  role?: keyof typeof RoleUtilisateur | null;
  isActive?: boolean | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  cv?: Pick<ICV, 'id'> | null;
}

export type NewUtilisateur = Omit<IUtilisateur, 'id'> & { id: null };
