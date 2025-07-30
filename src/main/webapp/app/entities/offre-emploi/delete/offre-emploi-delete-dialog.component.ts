import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IOffreEmploi } from '../offre-emploi.model';
import { OffreEmploiService } from '../service/offre-emploi.service';

@Component({
  templateUrl: './offre-emploi-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class OffreEmploiDeleteDialogComponent {
  offreEmploi?: IOffreEmploi;

  protected offreEmploiService = inject(OffreEmploiService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.offreEmploiService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
