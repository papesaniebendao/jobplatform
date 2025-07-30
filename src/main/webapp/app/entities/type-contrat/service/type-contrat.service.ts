import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITypeContrat, NewTypeContrat } from '../type-contrat.model';

export type PartialUpdateTypeContrat = Partial<ITypeContrat> & Pick<ITypeContrat, 'id'>;

export type EntityResponseType = HttpResponse<ITypeContrat>;
export type EntityArrayResponseType = HttpResponse<ITypeContrat[]>;

@Injectable({ providedIn: 'root' })
export class TypeContratService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/type-contrats');

  create(typeContrat: NewTypeContrat): Observable<EntityResponseType> {
    return this.http.post<ITypeContrat>(this.resourceUrl, typeContrat, { observe: 'response' });
  }

  update(typeContrat: ITypeContrat): Observable<EntityResponseType> {
    return this.http.put<ITypeContrat>(`${this.resourceUrl}/${this.getTypeContratIdentifier(typeContrat)}`, typeContrat, {
      observe: 'response',
    });
  }

  partialUpdate(typeContrat: PartialUpdateTypeContrat): Observable<EntityResponseType> {
    return this.http.patch<ITypeContrat>(`${this.resourceUrl}/${this.getTypeContratIdentifier(typeContrat)}`, typeContrat, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITypeContrat>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITypeContrat[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTypeContratIdentifier(typeContrat: Pick<ITypeContrat, 'id'>): number {
    return typeContrat.id;
  }

  compareTypeContrat(o1: Pick<ITypeContrat, 'id'> | null, o2: Pick<ITypeContrat, 'id'> | null): boolean {
    return o1 && o2 ? this.getTypeContratIdentifier(o1) === this.getTypeContratIdentifier(o2) : o1 === o2;
  }

  addTypeContratToCollectionIfMissing<Type extends Pick<ITypeContrat, 'id'>>(
    typeContratCollection: Type[],
    ...typeContratsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const typeContrats: Type[] = typeContratsToCheck.filter(isPresent);
    if (typeContrats.length > 0) {
      const typeContratCollectionIdentifiers = typeContratCollection.map(typeContratItem => this.getTypeContratIdentifier(typeContratItem));
      const typeContratsToAdd = typeContrats.filter(typeContratItem => {
        const typeContratIdentifier = this.getTypeContratIdentifier(typeContratItem);
        if (typeContratCollectionIdentifiers.includes(typeContratIdentifier)) {
          return false;
        }
        typeContratCollectionIdentifiers.push(typeContratIdentifier);
        return true;
      });
      return [...typeContratsToAdd, ...typeContratCollection];
    }
    return typeContratCollection;
  }
}
