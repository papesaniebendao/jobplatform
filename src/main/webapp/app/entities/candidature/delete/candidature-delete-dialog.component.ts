import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICandidature } from '../candidature.model';
import { CandidatureService } from '../service/candidature.service';

@Component({
  templateUrl: './candidature-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CandidatureDeleteDialogComponent {
  candidature?: ICandidature;

  protected candidatureService = inject(CandidatureService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.candidatureService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
