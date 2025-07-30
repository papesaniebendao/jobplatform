export interface ICV {
  id: number;
  urlFichier?: string | null;
}

export type NewCV = Omit<ICV, 'id'> & { id: null };
