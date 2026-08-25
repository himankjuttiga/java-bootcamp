terraform {
  required_version = ">= 1.5.0"
  required_providers {
    # Pin ranges. Replace null with a cloud/kubernetes provider only in an authorized sandbox.
    null = {
      source  = "hashicorp/null"
      version = "~> 3.2"
    }
  }
  # Remote state (documented in docs/ai-iac-review.md): a real backend keeps tfstate out of Git
  # with credentials via env / OIDC, never committed. Left unset here so init -backend=false works.
  # backend "s3" { … }
}
