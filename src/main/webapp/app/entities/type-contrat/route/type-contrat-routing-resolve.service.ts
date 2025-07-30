import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITypeContrat } from '../type-contrat.model';
import { TypeContratService } from '../service/type-contrat.service';

const typeContratResolve = (route: ActivatedRouteSnapshot): Observable<null | ITypeContrat> => {
  const id = route.params.id;
  if (id) {
    return inject(TypeContratService)
      .find(id)
      .pipe(
        mergeMap((typeContrat: HttpResponse<ITypeContrat>) => {
          if (typeContrat.body) {
            return of(typeContrat.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default typeContratResolve;
