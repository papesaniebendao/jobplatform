export interface ITypeContrat {
  id: number;
  nom?: string | null;
}

export type NewTypeContrat = Omit<ITypeContrat, 'id'> & { id: null };
