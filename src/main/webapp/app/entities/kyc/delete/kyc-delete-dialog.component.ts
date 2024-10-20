import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IKyc } from '../kyc.model';
import { KycService } from '../service/kyc.service';

@Component({
  standalone: true,
  templateUrl: './kyc-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class KycDeleteDialogComponent {
  kyc?: IKyc;

  protected kycService = inject(KycService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.kycService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
