import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICV } from '../cv.model';
import { CVService } from '../service/cv.service';
import { CVFormGroup, CVFormService } from './cv-form.service';

@Component({
  selector: 'jhi-cv-update',
  templateUrl: './cv-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CVUpdateComponent implements OnInit {
  isSaving = false;
  cV: ICV | null = null;

  protected cVService = inject(CVService);
  protected cVFormService = inject(CVFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CVFormGroup = this.cVFormService.createCVFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cV }) => {
      this.cV = cV;
      if (cV) {
        this.updateForm(cV);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cV = this.cVFormService.getCV(this.editForm);
    if (cV.id !== null) {
      this.subscribeToSaveResponse(this.cVService.update(cV));
    } else {
      this.subscribeToSaveResponse(this.cVService.create(cV));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICV>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(cV: ICV): void {
    this.cV = cV;
    this.cVFormService.resetForm(this.editForm, cV);
  }
}
