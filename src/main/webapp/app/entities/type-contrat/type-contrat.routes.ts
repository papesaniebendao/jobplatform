import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TypeContratResolve from './route/type-contrat-routing-resolve.service';

const typeContratRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/type-contrat.component').then(m => m.TypeContratComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/type-contrat-detail.component').then(m => m.TypeContratDetailComponent),
    resolve: {
      typeContrat: TypeContratResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/type-contrat-update.component').then(m => m.TypeContratUpdateComponent),
    resolve: {
      typeContrat: TypeContratResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/type-contrat-update.component').then(m => m.TypeContratUpdateComponent),
    resolve: {
      typeContrat: TypeContratResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default typeContratRoute;
