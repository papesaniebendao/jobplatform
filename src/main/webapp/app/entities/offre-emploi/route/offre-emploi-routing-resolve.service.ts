import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOffreEmploi } from '../offre-emploi.model';
import { OffreEmploiService } from '../service/offre-emploi.service';

const offreEmploiResolve = (route: ActivatedRouteSnapshot): Observable<null | IOffreEmploi> => {
  const id = route.params.id;
  if (id) {
    return inject(OffreEmploiService)
      .find(id)
      .pipe(
        mergeMap((offreEmploi: HttpResponse<IOffreEmploi>) => {
          if (offreEmploi.body) {
            return of(offreEmploi.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default offreEmploiResolve;
