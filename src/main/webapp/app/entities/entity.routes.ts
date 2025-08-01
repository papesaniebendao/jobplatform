import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'jobplatformApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'utilisateur',
    data: { pageTitle: 'jobplatformApp.utilisateur.home.title' },
    loadChildren: () => import('./utilisateur/utilisateur.routes'),
  },
  {
    path: 'cv',
    data: { pageTitle: 'jobplatformApp.cV.home.title' },
    loadChildren: () => import('./cv/cv.routes'),
  },
  {
    path: 'type-contrat',
    data: { pageTitle: 'jobplatformApp.typeContrat.home.title' },
    loadChildren: () => import('./type-contrat/type-contrat.routes'),
  },
  {
    path: 'offre-emploi',
    data: { pageTitle: 'jobplatformApp.offreEmploi.home.title' },
    loadChildren: () => import('./offre-emploi/offre-emploi.routes'),
  },
  {
    path: 'candidature',
    data: { pageTitle: 'jobplatformApp.candidature.home.title' },
    loadChildren: () => import('./candidature/candidature.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
